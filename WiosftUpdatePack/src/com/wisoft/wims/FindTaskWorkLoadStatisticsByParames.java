package com.wisoft.wims;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for findTaskWorkLoadStatisticsByParames complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;findTaskWorkLoadStatisticsByParames&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}anyType&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;arg0&quot; type=&quot;{http://www.springframework.org/schema/beans}workLoadStatisticsBySearchParamesServicesIn&quot; minOccurs=&quot;0&quot;/&gt;
 *         &lt;element name=&quot;arg1&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *         &lt;element name=&quot;arg2&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot;/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "findTaskWorkLoadStatisticsByParames", propOrder = { "arg0",
		"arg1", "arg2" })
public class FindTaskWorkLoadStatisticsByParames {

	protected WorkLoadStatisticsBySearchParamesServicesIn arg0;
	protected int arg1;
	protected int arg2;

	/**
	 * Gets the value of the arg0 property.
	 * 
	 * @return possible object is
	 *         {@link WorkLoadStatisticsBySearchParamesServicesIn }
	 * 
	 */
	public WorkLoadStatisticsBySearchParamesServicesIn getArg0() {
		return arg0;
	}

	/**
	 * Sets the value of the arg0 property.
	 * 
	 * @param value
	 *            allowed object is
	 *            {@link WorkLoadStatisticsBySearchParamesServicesIn }
	 * 
	 */
	public void setArg0(WorkLoadStatisticsBySearchParamesServicesIn value) {
		this.arg0 = value;
	}

	/**
	 * Gets the value of the arg1 property.
	 * 
	 */
	public int getArg1() {
		return arg1;
	}

	/**
	 * Sets the value of the arg1 property.
	 * 
	 */
	public void setArg1(int value) {
		this.arg1 = value;
	}

	/**
	 * Gets the value of the arg2 property.
	 * 
	 */
	public int getArg2() {
		return arg2;
	}

	/**
	 * Sets the value of the arg2 property.
	 * 
	 */
	public void setArg2(int value) {
		this.arg2 = value;
	}

}