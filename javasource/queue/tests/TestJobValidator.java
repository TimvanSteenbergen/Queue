package queue.tests;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Test;

import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;

import queue.helpers.JobValidator;
import queue.helpers.MicroflowValidator;
import queue.proxies.ENU_TimeUnit;
import queue.proxies.Job;
import queue.repositories.QueueRepository;

public class TestJobValidator {

	ILogNode logger = mock(ILogNode.class);
	MicroflowValidator microflowValidator = mock(MicroflowValidator.class);
	IContext context = mock(IContext.class);
	QueueRepository queueRepository = mock(QueueRepository.class);
	Job job = mock(Job.class);
	JobValidator jobValidator = new JobValidator(logger, microflowValidator);
	@SuppressWarnings("rawtypes")
	Set microflowNames = mock(Set.class);
	
	@Test
	public void validateTrue() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertTrue(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(job, times(1)).getBaseDelay(context);
		verify(job, times(1)).getCurrentDelay(context);
		verify(job, times(1)).getDelayUnit(context);
		verify(job, times(1)).getMaxRetries(context);
		verify(job, times(1)).getMicroflowName(context);
		verify(job, times(1)).getRetry(context);
	}
	
	@Test
	public void validateFalseQueueEmpty() {
		String validQueueName = "";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(validMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(logger,times(1)).error("Queue is missing in Job object.");
	}
	
	@Test
	public void validateFalseQueueNull() {
		String validQueueName = null;
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(validMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(logger,times(1)).error("Queue is missing in Job object.");
	}
	
	@Test
	public void validateFalseQueueDoesNotExist() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(validMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(false);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(logger,times(1)).error("Queue with name " + validQueueName +  " has not been initialized.");
	}
	
	@Test
	public void validateFalseMicroflowEmpty() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(validMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(job, times(1)).getMicroflowName(context);
		verify(logger, times(1)).error("MicroflowName is missing in Job object.");
	}
	
	@Test
	public void validateFalseMicroflowNull() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = null;
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(validMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(job, times(1)).getMicroflowName(context);
		verify(logger, times(1)).error("MicroflowName is missing in Job object.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseMicroflowDoesNotExistNoSuggestions() {
		String validQueueName = "ValidQueueName";
		String invalidMicroflowName = "InvalidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(invalidMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(invalidMicroflowName, logger)).thenReturn(false);
		when(microflowValidator.getClosestMatch(invalidMicroflowName, microflowNames)).thenReturn("");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(job, times(1)).getMicroflowName(context);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseMicroflowDoesNotExistSuggestions() {
		String validQueueName = "ValidQueueName";
		String invalidMicroflowName = "InvalidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn(invalidMicroflowName);
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(invalidMicroflowName, logger)).thenReturn(false);
		when(microflowValidator.getClosestMatch(invalidMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getQueue(context);
		verify(job, times(1)).getMicroflowName(context);
	}

	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseRetryNegative() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(-1);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		when(microflowValidator.getClosestMatch(validMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getRetry(context);
		verify(logger, times(1)).error("Retry of -1 is invalid and should be a number larger than or equal to 0.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseDelayUnitNull () {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(null);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		when(microflowValidator.getClosestMatch(validMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getDelayUnit(context);
		verify(logger, times(1)).error("DelayUnit cannot be empty.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseMaxRetriesNegative() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(0);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(-3);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		when(microflowValidator.getClosestMatch(validMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getMaxRetries(context);
		verify(logger, times(1)).error("MaxRetries of -3 is invalid and should be a number larger than or equal to 0.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseCurrentDelayNegative() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(500);
		when(job.getCurrentDelay(context)).thenReturn(-800);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		when(microflowValidator.getClosestMatch(validMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getMaxRetries(context);
		verify(logger, times(1)).error("CurrentDelay of -800 is invalid and should be a number larger than or equal to 0.");
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void validateFalseBaseDelayNegative() {
		String validQueueName = "ValidQueueName";
		String validMicroflowName = "ValidMicroflowName";
		when(job.getQueue(context)).thenReturn(validQueueName);
		when(job.getBaseDelay(context)).thenReturn(-500);
		when(job.getCurrentDelay(context)).thenReturn(300);
		when(job.getDelayUnit(context)).thenReturn(ENU_TimeUnit.Milliseconds);
		when(job.getMaxRetries(context)).thenReturn(5);
		when(job.getMicroflowName(context)).thenReturn("ValidMicroflowName");
		when(job.getRetry(context)).thenReturn(0);
		
		when(queueRepository.queueExists(validQueueName)).thenReturn(true);
		when(microflowValidator.validate(validMicroflowName, logger)).thenReturn(true);
		when(microflowValidator.getClosestMatch(validMicroflowName, microflowNames)).thenReturn("ValidMicroflowName");
		
		boolean actualResult = jobValidator.isValid(context, queueRepository, job);
		assertFalse(actualResult);
		verify(job, times(1)).getMaxRetries(context);
		verify(logger, times(1)).error("BaseDelay of -500 is invalid and should be a number larger than or equal to 0.");
	}
}
