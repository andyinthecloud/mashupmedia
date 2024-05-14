package org.mashupmedia.model.media;

import java.io.Serializable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "years")
@Cacheable
public class Year implements Serializable {
	private static final long serialVersionUID = -6346257354674712995L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "years_generator")
	@SequenceGenerator(name = "years_generator", sequenceName = "years_seq", allocationSize = 1)
	private long id;
	@Column(name = "year_number")
	private int year;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Year other = (Year) obj;
		if (year != other.year)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Year [id=");
		builder.append(id);
		builder.append(", year=");
		builder.append(year);
		builder.append("]");
		return builder.toString();
	}

}
