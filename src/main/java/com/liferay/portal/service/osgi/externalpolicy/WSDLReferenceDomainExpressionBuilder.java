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

	private static final Collection<QName> SUPPORTED_TYPES = Arrays.asList(
		new QName("http://liferay.com/2014/04/addressing", "Service"),
		new QName("http://liferay.com/2014/04/addressing", "Endpoint"),
		new QName("http://liferay.com/2014/04/addressing", "BindingOperation"),
		new QName("http://liferay.com/2014/04/addressing", "BindingMessage"),
		new QName("http://liferay.com/2014/04/addressing", "BindinFault")
		);

	@Override
	public DomainExpression build(Element e) {
		String localName = e.getLocalName();

		// TODO: support all kinds of mapping

		String re = e.getTextContent().trim();

		return new WSDLReferenceDomainExpression(Pattern.compile(re));
	}

	@Override
	public Collection<QName> getDomainExpressionTypes() {
		return SUPPORTED_TYPES;
	}
}
