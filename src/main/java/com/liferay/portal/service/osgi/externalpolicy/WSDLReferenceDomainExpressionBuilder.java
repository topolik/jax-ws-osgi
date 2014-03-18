package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.ws.policy.attachment.external.DomainExpression;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilder;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Tomas Polesovsky
 */
public class WSDLReferenceDomainExpressionBuilder implements DomainExpressionBuilder {

	private static final QName SERVICE_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "Service");

	private static final QName ENDPOINT_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "Endpoint");

	private static final QName BINDING_OPERATION_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindingOperation");

	private static final QName BINDING_MESSAGE_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindingMessage");

	private static final QName BINDING_FAULT_REFERENCE =
		new QName("http://liferay.com/2014/04/addressing", "BindinFault");

	private static final Collection<QName> SUPPORTED_TYPES = Arrays.asList(
		SERVICE_REFERENCE, ENDPOINT_REFERENCE, BINDING_OPERATION_REFERENCE,
		BINDING_MESSAGE_REFERENCE,BINDING_FAULT_REFERENCE);

	@Override
	public DomainExpression build(Element e) {
		QName name = new QName(e.getNamespaceURI(), e.getLocalName());

		WSDLReferenceDomainExpression expression = null;

		if (name.equals(SERVICE_REFERENCE)) {
			expression = buildServiceReference(e);
		}
		if (name.equals(ENDPOINT_REFERENCE)) {
			expression = buildEndpointReference(e);
		}
		if (name.equals(BINDING_OPERATION_REFERENCE)) {
			expression = buildBindingOperationReference(e);
		}
		if (name.equals(BINDING_MESSAGE_REFERENCE)) {
			expression = buildBindingMessageReference(e);
		}
		if (name.equals(BINDING_FAULT_REFERENCE)) {
			expression = buildBindingFaultReference(e);
		}

		return expression;
	}

	@Override
	public Collection<QName> getDomainExpressionTypes() {
		return SUPPORTED_TYPES;
	}


	protected WSDLReferenceDomainExpression buildBindingFaultReference(Element e) {
		return null;
	}

	protected WSDLReferenceDomainExpression buildBindingMessageReference(Element e) {
		return null;
	}

	protected WSDLReferenceDomainExpression buildBindingOperationReference(Element e) {
		return null;
	}

	protected WSDLReferenceDomainExpression buildEndpointReference(Element e) {
		return null;
	}

	protected WSDLReferenceDomainExpression buildServiceReference(Element e) {
		return null;
	}

}
