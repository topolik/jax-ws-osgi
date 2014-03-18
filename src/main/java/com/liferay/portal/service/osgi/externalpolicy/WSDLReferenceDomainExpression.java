package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.ws.policy.attachment.external.DomainExpression;

import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class WSDLReferenceDomainExpression implements DomainExpression {
	private Pattern _endpointRegExp;

	public WSDLReferenceDomainExpression(Pattern _endpointRegExp) {
		this._endpointRegExp = _endpointRegExp;
	}

	@Override
	public boolean appliesTo(BindingFaultInfo bfi) {
		return false;
	}

	@Override
	public boolean appliesTo(BindingMessageInfo bmi) {
		return false; // LiferayWSPolicyFeature:37
	}

	@Override
	public boolean appliesTo(BindingOperationInfo boi) {
		return false; // LiferayWSPolicyFeature:37
	}

	@Override
	public boolean appliesTo(EndpointInfo ei) {
		return _endpointRegExp.matcher(ei.getAddress()).matches(); // EndpointPolicyImpl:151
	}

	@Override
	public boolean appliesTo(ServiceInfo si) {
		return false; // EndpointPolicyImpl:150
	}

	private String _endpointAddress;
	private Pattern _endpointAddressRE;

	private String _serviceName;
	private Pattern _serviceNameRE;

	private String _bindingOperation;
	private Pattern _bindingOperationRE;

}
