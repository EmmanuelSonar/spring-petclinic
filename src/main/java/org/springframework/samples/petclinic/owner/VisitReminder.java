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

/**
 * Read-only projection describing an upcoming {@link Visit} that an {@link Owner} should
 * be reminded about. Populated by a JPQL constructor expression in
 * {@link OwnerRepository#findVisitRemindersForDate(LocalDate)}.
 *
 * @param ownerFirstName the owner's first name
 * @param ownerLastName the owner's last name
 * @param email the owner's email address (never {@code null} for results)
 * @param petName the name of the pet the visit is for
 * @param visitId the identifier of the visit
 * @param date the date of the visit
 * @param description the visit description
 */
public record VisitReminder(String ownerFirstName, String ownerLastName, String email, String petName, Integer visitId,
		LocalDate date, String description) {
}
