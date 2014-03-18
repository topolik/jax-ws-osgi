package com.liferay.portal.service.osgi.externalpolicy;

import org.apache.cxf.Bus;
import org.apache.cxf.common.i18n.BundleUtils;
import org.apache.cxf.common.i18n.Message;
import org.apache.cxf.service.model.BindingFaultInfo;
import org.apache.cxf.service.model.BindingMessageInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.StaxUtils;
import org.apache.cxf.ws.policy.PolicyConstants;
import org.apache.cxf.ws.policy.PolicyException;
import org.apache.cxf.ws.policy.attachment.AbstractPolicyProvider;
import org.apache.cxf.ws.policy.attachment.external.DomainExpression;
import org.apache.cxf.ws.policy.attachment.external.DomainExpressionBuilderRegistry;
import org.apache.cxf.ws.policy.attachment.external.ExternalAttachmentProvider;
import org.apache.cxf.ws.policy.attachment.external.PolicyAttachment;
import org.apache.cxf.ws.policy.attachment.reference.LocalDocumentReferenceResolver;
import org.apache.cxf.ws.policy.attachment.reference.ReferenceResolver;
import org.apache.neethi.Constants;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * @author Tomas Polesovsky
 */
public class LiferayExternalAttachmentProvider extends AbstractPolicyProvider {

	private static final ResourceBundle BUNDLE = BundleUtils.getBundle(ExternalAttachmentProvider.class);

	// Use a Resource object here instead of a String so that the resource can be resolved when
	// this bean is created

	private URL location;
	private Collection<PolicyAttachment> attachments;

	public LiferayExternalAttachmentProvider() {
	}

	public LiferayExternalAttachmentProvider(Bus b) {
		super(b);
	}

	public void setLocation(URL u) {
		location = u;
	}

	public Policy getEffectivePolicy(BindingFaultInfo bfi) {
		readDocument();
		Policy p = null;
		for (PolicyAttachment pa : attachments) {
			if (pa.appliesTo(bfi)) {
				if (p == null) {
					p = new Policy();
				}
				p = p.merge(pa.getPolicy());
			}
		}

		return p;
	}

	public Policy getEffectivePolicy(BindingMessageInfo bmi) {
		readDocument();
		Policy p = null;
		for (PolicyAttachment pa : attachments) {
			if (pa.appliesTo(bmi)) {
				if (p == null) {
					p = new Policy();
				}
				p = p.merge(pa.getPolicy());
			}
		}
		return p;
	}

	public Policy getEffectivePolicy(BindingOperationInfo boi) {
		readDocument();
		Policy p = null;
		for (PolicyAttachment pa : attachments) {
			if (pa.appliesTo(boi)) {
				if (p == null) {
					p = new Policy();
				}
				p = p.merge(pa.getPolicy());
			}
		}

		return p;
	}

	public Policy getEffectivePolicy(EndpointInfo ei) {
		readDocument();
		Policy p = null;
		for (PolicyAttachment pa : attachments) {
			if (pa.appliesTo(ei)) {
				if (p == null) {
					p = new Policy();
				}
				p = p.merge(pa.getPolicy());
			}
		}

		return p;
	}

	public Policy getEffectivePolicy(ServiceInfo si) {
		readDocument();
		Policy p = null;
		for (PolicyAttachment pa : attachments) {
			if (pa.appliesTo(si)) {
				if (p == null) {
					p = new Policy();
				}
				p = p.merge(pa.getPolicy());
			}
		}

		return p;
	}

	void readDocument() {
		if (null != attachments) {
			return;
		}

		// read the document and build the attachments
		attachments = new ArrayList<PolicyAttachment>();
		Document doc = null;
		try {
			InputStream is = location.openStream();
			if (null == is) {
				throw new PolicyException(new Message("COULD_NOT_OPEN_ATTACHMENT_DOC_EXC", BUNDLE, location));
			}
			doc = StaxUtils.read(is);
		} catch (Exception ex) {
			throw new PolicyException(ex);
		}

		for (Element ae
			: PolicyConstants
			.findAllPolicyElementsOfLocalName(doc,
				Constants.ELEM_POLICY_ATTACHMENT)) {
			PolicyAttachment attachment = new PolicyAttachment();

			for (Node nd = ae.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
				if (Node.ELEMENT_NODE != nd.getNodeType()) {
					continue;
				}

				QName qn = new QName(nd.getNamespaceURI(), nd.getLocalName());
				if (Constants.isAppliesToElem(qn)) {
					Collection<DomainExpression> des = readDomainExpressions((Element)nd);
					if (des.isEmpty()) {
						// forget about this attachment
						continue;
					}
					attachment.setDomainExpressions(des);
				} else if (Constants.isPolicyElement(qn)) {
					Policy p = builder.getPolicy(nd);
					if (null != attachment.getPolicy()) {
						p = p.merge(attachment.getPolicy());
					}
					attachment.setPolicy(p);

					// cache the element so it can be used when generating the wsdl
					attachment.setElement((Element) nd);

				} else if (Constants.isPolicyRef(qn)) {
					PolicyReference ref = builder.getPolicyReference(nd);
					if (null != ref) {
						Policy p = resolveReference(ref, doc);
						if (null != attachment.getPolicy()) {
							p = p.merge(attachment.getPolicy());
						}
						attachment.setPolicy(p);
					}
				} // TODO: wsse:Security child element
			}

			if (null == attachment.getPolicy() || null == attachment.getDomainExpressions()) {
				continue;
			}

			attachments.add(attachment);
		}
	}

	Policy resolveReference(PolicyReference ref, Document doc) {
		Policy p = null;
		if (isExternal(ref)) {
			p = resolveExternal(ref, doc.getBaseURI());
		} else {
			p = resolveLocal(ref, doc);
		}
		checkResolved(ref, p);
		return p;
	}

	Policy resolveLocal(PolicyReference ref, Document doc) {
		String relativeURI = ref.getURI().substring(1);
		String absoluteURI = doc.getBaseURI() + ref.getURI();
		Policy resolved = registry.lookup(absoluteURI);
		if (null != resolved) {
			return resolved;
		}
		ReferenceResolver resolver = new LocalDocumentReferenceResolver(doc, builder);
		resolved = resolver.resolveReference(relativeURI);
		if (null != resolved) {
			ref.setURI(absoluteURI);
			registry.register(absoluteURI, resolved);
		}
		return resolved;
	}

	Collection<DomainExpression> readDomainExpressions(Element appliesToElem) {
		Collection<DomainExpression> des = new ArrayList<DomainExpression>();
		for (Node nd = appliesToElem.getFirstChild(); nd != null; nd = nd.getNextSibling()) {
			if (Node.ELEMENT_NODE == nd.getNodeType()) {
				DomainExpressionBuilderRegistry debr
					= bus.getExtension(DomainExpressionBuilderRegistry.class);
				assert null != debr;
				DomainExpression de = debr.build((Element)nd);
				des.add(de);
			}
		}
		return des;
	}

	// for test

	void setAttachments(Collection<PolicyAttachment> a) {
		attachments = a;
	}

	public Collection<PolicyAttachment> getAttachments() {
		return attachments;
	}


}
