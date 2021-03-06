package com.wisoft.wims;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;

import wisoft.pack.utils.PackConfigInfo;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.3-hudson-390-
 * Generated source version: 2.0
 * <p>
 * An example of how this class may be used:
 * 
 * <pre>
 * TrankingManager service = new TrankingManager();
 * IWimsManagerWSPortType portType = service.getTrackingManager();
 * portType.findTaskByServicesInParames(...);
 * </pre>
 * 
 * </p>
 * 
 */
@WebServiceClient(name = "TrankingManager", targetNamespace = "http://www.springframework.org/schema/beans", wsdlLocation = "")
public class TrankingManager extends Service {

	private final static URL TRANKINGMANAGER_WSDL_LOCATION;
	private final static Logger logger = Logger
			.getLogger(com.wisoft.wims.TrankingManager.class.getName());

	static {
		URL url = null;
		try {
			URL baseUrl;
			baseUrl = com.wisoft.wims.TrankingManager.class.getResource(".");
			url = new URL(baseUrl,
					PackConfigInfo.getInstance().getWimsTrackManagerPath());
		} catch (MalformedURLException e) {
			logger
					.warning("Failed to create URL for the wsdl Location: '"+PackConfigInfo.getInstance().getWimsTrackManagerPath()+"', retrying as a local file");
			logger.warning(e.getMessage());
		}
		TRANKINGMANAGER_WSDL_LOCATION = url;
	}

	public TrankingManager(URL wsdlLocation, QName serviceName) {
		super(wsdlLocation, serviceName);
	}

	public TrankingManager() {
		super(TRANKINGMANAGER_WSDL_LOCATION, new QName(
				"http://www.springframework.org/schema/beans",
				"TrankingManager"));
	}

	/**
	 * 
	 * @return returns IWimsManagerWSPortType
	 */
	@WebEndpoint(name = "TrackingManager")
	public IWimsManagerWSPortType getTrackingManager() {
		return super.getPort(new QName(
				"http://www.springframework.org/schema/beans",
				"TrackingManager"), IWimsManagerWSPortType.class);
	}

}
