/*
 * Copyright (c) 2010 Thorsten Berger <berger@informatik.uni-leipzig.de>
 *
 * This file is part of CDLTools.
 *
 * CDLTools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CDLTools is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CDLTools.  If not, see <http://www.gnu.org/licenses/>.
 */

package gsd.cdl;

public class EcosNode {

	private String name;
	private String title;
	private String description;
	private String parent;
	private int optionType;
	private boolean bool;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}
	/**
	 * @param parent the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}
	/**
	 * @return the optionType
	 */
	public int getOptionType() {
		return optionType;
	}
	/**
	 * @param optionType the optionType to set
	 */
	public void setOptionType(int optionType) {
		this.optionType = optionType;
	}
	/**
	 * @return the bool
	 */
	public boolean isBool() {
		return bool;
	}
	/**
	 * @param bool the bool to set
	 */
	public void setBool(boolean bool) {
		this.bool = bool;
	}
	/**
	 * @param name
	 * @param title
	 * @param description
	 * @param parent
	 * @param optionType
	 * @param bool
	 */
	public EcosNode(String name, String title, String description,
			String parent, int optionType, boolean bool) {
		super();
		this.name = name;
		this.title = title;
		this.description = description;
		this.parent = parent;
		this.optionType = optionType;
		this.bool = bool;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + ":" +getParent();
	}
	
	

}
