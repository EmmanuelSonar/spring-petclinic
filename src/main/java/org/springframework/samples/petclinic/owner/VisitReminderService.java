/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Sends reminders to owners ahead of their pets' visits. A scheduled job runs once a day
 * and notifies the owner of every non-cancelled visit taking place
 * {@value #REMINDER_LEAD_DAYS} days from now.
 * <p>
 * For this demonstration the "notification" is written to the application log rather than
 * sent over SMTP; swapping in a real {@code JavaMailSender} only requires changing
 * {@link #notifyOwner(VisitReminder)}.
 */
@Component
public class VisitReminderService {

	/**
	 * Number of days before a visit that the owner is reminded.
	 */
	static final int REMINDER_LEAD_DAYS = 3;

	private static final Logger log = LoggerFactory.getLogger(VisitReminderService.class);

	private final OwnerRepository owners;

	private final VisitRepository visits;

	public VisitReminderService(OwnerRepository owners, VisitRepository visits) {
		this.owners = owners;
		this.visits = visits;
	}

	/**
	 * Daily job (08:00 by default, overridable via the {@code petclinic.reminder.cron}
	 * property) that emails owners about visits happening in {@value #REMINDER_LEAD_DAYS}
	 * days.
	 */
	@Scheduled(cron = "${petclinic.reminder.cron:0 0 8 * * *}")
	@Transactional
	public void sendVisitReminders() {
		LocalDate targetDate = LocalDate.now().plusDays(REMINDER_LEAD_DAYS);
		List<VisitReminder> reminders = this.owners.findVisitRemindersForDate(targetDate);
		if (reminders.isEmpty()) {
			return;
		}

		for (VisitReminder reminder : reminders) {
			notifyOwner(reminder);
		}
		this.visits.markRemindersSent(reminders.stream().map(VisitReminder::visitId).toList());
		log.info("Sent {} visit reminder(s) for {}", reminders.size(), targetDate);
	}

	private void notifyOwner(VisitReminder reminder) {
		log.info(
				"Visit reminder -> to: {} <{}>, pet: {}, date: {}, description: {}. "
						+ "Your pet has a visit scheduled in {} days.",
				reminder.ownerFirstName() + " " + reminder.ownerLastName(), reminder.email(), reminder.petName(),
				reminder.date(), reminder.description(), REMINDER_LEAD_DAYS);
	}

}
