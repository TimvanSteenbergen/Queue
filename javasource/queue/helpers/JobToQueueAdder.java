package queue.helpers;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.mendix.core.CoreException;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.systemwideinterfaces.core.IMendixObject;

import queue.proxies.ENU_JobStatus;
import queue.proxies.Job;
import queue.repositories.ScheduledJobRepository;
import queue.utilities.CoreUtility;
import queue.repositories.ConstantsRepository;
import queue.repositories.JobRepository;
import queue.repositories.MicroflowRepository;
import queue.repositories.QueueRepository;

public class JobToQueueAdder {
	
	private JobValidator jobValidator;
	private ExponentialBackoffCalculator exponentialBackoffCalculator;
	private TimeUnitConverter timeUnitConverter;
	private ConstantsRepository constantsRepository;
	private CoreUtility coreUtility;
	private MicroflowRepository microflowRepository;
	
	public JobToQueueAdder(JobValidator jobValidator, ExponentialBackoffCalculator exponentialBackoffCalculator, TimeUnitConverter timeUnitConverter, ConstantsRepository constantsRepository, CoreUtility coreUtility, MicroflowRepository microflowRepository) {
		this.jobValidator = jobValidator;
		this.exponentialBackoffCalculator = exponentialBackoffCalculator;
		this.timeUnitConverter = timeUnitConverter;
		this.constantsRepository = constantsRepository;
		this.coreUtility = coreUtility;
		this.microflowRepository = microflowRepository;
	}
	
	public void add(IContext context, ILogNode logger, QueueRepository queueRepository, JobRepository jobRepository, ScheduledJobRepository scheduledJobRepository, Job job) throws CoreException {
		boolean valid = this.jobValidator.isValid(context, queueRepository, job);
		
		if (valid == false) {
			throw new CoreException("Job is not added, because it could not be validated.");
		}
		
		ScheduledExecutorService executor = queueRepository.getQueue(job.getQueue(context));
		
		if(executor == null) {
			throw new CoreException("Queue with name " + job.getQueue(context) + " could not be found. Job has not been added.");
		}
		
		if(executor.isShutdown() || executor.isTerminated()) {
			throw new CoreException("Queue with name " + job.getQueue(context) + " has already been shut down or terminated. Job has not been added.");
		}
		
		long instanceIndex = coreUtility.getInstanceIndex();
		
		if(constantsRepository.isClusterSupport() && instanceIndex >= 0) {
			job.setInstanceIndex(context, (int) instanceIndex);
		}
	
		job.setStatus(context, ENU_JobStatus.Queued);
		
		try {
			job.commit(context);
		} catch (Exception e) {
			throw new CoreException("Could not commit job.", e);
		}
		
		IMendixObject jobObject = job.getMendixObject();
				
		ScheduledFuture<?> future =	executor.schedule(
					queueRepository.getQueueHandler(logger, this, scheduledJobRepository, queueRepository, jobRepository, microflowRepository, jobObject.getId()), 
					job.getCurrentDelay(context), 
					timeUnitConverter.getTimeUnit(job.getDelayUnit(context).getCaption("en_US"))
					);
		
		scheduledJobRepository.add(context, jobObject, future);
	}
	
	public void addWithMicroflow(IContext context, ILogNode logger, QueueRepository queueRepository, JobRepository jobRepository, ScheduledJobRepository scheduledJobRepository, Job job, String microflow) throws CoreException {
		job.setMicroflowName(context, microflow);
		add(context, logger, queueRepository, jobRepository, scheduledJobRepository, job);
	}
	
	public void addRetry(IContext context, ILogNode logger, QueueRepository queueRepository, JobRepository jobRepository, ScheduledJobRepository scheduledJobRepository, Job job) throws CoreException {
		int retry = job.getRetry(context);
		int baseDelay = job.getBaseDelay(context);
		int newDelay= this.exponentialBackoffCalculator.calculate(baseDelay, retry);
		job.setCurrentDelay(context, newDelay);
		job.setRetry(context, retry + 1);
		add(context, logger, queueRepository, jobRepository, scheduledJobRepository, job);
	}
	
	public ExponentialBackoffCalculator getExponentialBackoffCalculator() {
		return this.exponentialBackoffCalculator;
	}
	
	public void setTimeZone(IContext context, ILogNode logger) {
		String timeZoneID = constantsRepository.getTimeZoneID();
		
		if (timeZoneID == null || timeZoneID.equals("")) {
			return;
		}
		
		List<String> timeZoneList = Arrays.asList(TimeZone.getAvailableIDs());
		boolean timeZoneExists = timeZoneList.stream().anyMatch(
				tz -> tz.equals(timeZoneID)
				);
		boolean timeZoneDoesNotExist = (timeZoneExists == false);
		
		if(timeZoneDoesNotExist) {
			logger.warn("TimeZoneID " + timeZoneID + " is not valid. No time zone will be configured.");
			return;
		}
		
		boolean useDstCorrection = constantsRepository.useDstIfAppliccable();
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneID); 
		int offset  = timeZone.getRawOffset();
		
		if (useDstCorrection) {
			
			boolean inDaylightSavingTime = timeZone.inDaylightTime(new Date());
			int dstCorrection = timeZone.getDSTSavings();
			
			if (inDaylightSavingTime) {
				offset = offset + dstCorrection;
			}
			
		}
		
		context.getSession().setTimeZone(-offset/1000/60);
	}
}