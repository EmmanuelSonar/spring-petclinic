package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;

/**
 * A simple POJO representing the data submitted when creating a new {@link Visit}. Using
 * a DTO instead of the persistent entity prevents mass assignment vulnerabilities.
 */
class VisitDTO {

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;

	@NotBlank
	private String description;

	VisitDTO() {
		this.date = LocalDate.now().plusDays(1);
	}

	public LocalDate getDate() {
		return this.date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns {@code true} to indicate this DTO always represents a new visit. Used by
	 * the Thymeleaf template via {@code visit['new']}.
	 * @return always {@code true}
	 */
	public boolean isNew() {
		return true;
	}

}
