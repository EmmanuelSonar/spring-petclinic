package org.springframework.samples.petclinic.owner;

/**
 * Simple POJO used as a form-backing object for the owner search form, avoiding direct
 * binding to the persistent {@link Owner} entity.
 */
class OwnerFindForm {

	private String lastName;

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

}
