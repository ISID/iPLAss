package org.iplass.mtp.impl.webhook.template;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.iplass.mtp.definition.DefinitionModifyResult;
import org.iplass.mtp.impl.auth.authenticate.token.AuthTokenService;
import org.iplass.mtp.impl.core.ExecuteContext;
import org.iplass.mtp.impl.definition.AbstractTypedDefinitionManager;
import org.iplass.mtp.impl.definition.TypedMetaDataService;
import org.iplass.mtp.impl.metadata.RootMetaData;
import org.iplass.mtp.impl.webhook.WebHookAuthTokenHandler;
import org.iplass.mtp.impl.webhook.WebHookService;
import org.iplass.mtp.spi.ServiceRegistry;
import org.iplass.mtp.webhook.template.definition.WebHookSubscriber;
import org.iplass.mtp.webhook.template.definition.WebHookTemplateDefinition;
import org.iplass.mtp.webhook.template.definition.WebHookTemplateDefinitionManager;
import org.iplass.mtp.webhook.template.definition.WebHookSubscriber.WEBHOOKSUBSCRIBERSTATE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebHookTemplateDefinitionManagerImpl extends AbstractTypedDefinitionManager<WebHookTemplateDefinition> implements
WebHookTemplateDefinitionManager{


	private static final Logger logger = LoggerFactory.getLogger(WebHookTemplateDefinitionManager.class);
	private WebHookService service;
	
	public WebHookTemplateDefinitionManagerImpl() {
		this.service = ServiceRegistry.getRegistry().getService(WebHookService.class);
	}

	@Override
	public Class<WebHookTemplateDefinition> getDefinitionType() {
		return WebHookTemplateDefinition.class;
	}

	@Override
	protected RootMetaData newInstance(WebHookTemplateDefinition definition) {
		return new MetaWebHookTemplate();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected TypedMetaDataService getService() {
		return service;
	}

	//セキュリティ関連の情報をデータベースに入れるためoverrideします
	@Override
	public WebHookTemplateDefinition get(String definitionName) {
		WebHookTemplateDefinition definition = super.get(definitionName);
		WebHookService ws = (WebHookService) getService();
		//データベースで情報を取得
		try {
		definition = ws.fillSubscriberListByDef(definition);
		} catch (Exception e) {
			logger.warn("Exception occured when retrieving Subscriber info. Caused by: "+e.getCause()+". With following message : "+e.getCause().getMessage());
		}
		return definition;
	}

	@Override
	public DefinitionModifyResult create(WebHookTemplateDefinition definition) {
		//データベースで情報を保存
		WebHookService ws = (WebHookService) getService();
		try {
		definition = ws.updateSubscriberListByDef(definition);
		} catch (Exception e) {
			setRollbackOnly();
			String msg = "Exception occured when saving Subscriber info. Caused by: "+e.getCause()+". With following message : "+e.getCause().getMessage()+" .The process will roll back.";
			logger.warn(msg);
			return new DefinitionModifyResult(false, msg);
		}
		return super.create(definition);
	}

	@Override
	public DefinitionModifyResult update(WebHookTemplateDefinition definition) {
		WebHookService ws = (WebHookService) getService();
		//データベースで情報を保存
		try {
		definition = ws.updateSubscriberListByDef(definition);
		} catch (Exception e) {
			setRollbackOnly();
			String msg = "Exception occured when saving Subscriber info. Caused by: "+e.getCause()+". With following message : "+e.getCause().getMessage()+" .The process will roll back.";
			logger.warn(msg);
			return new DefinitionModifyResult(false, msg);
		}
		return super.update(definition);
	}
	
	@Override
	public DefinitionModifyResult remove(String definitionName) {
		WebHookTemplateDefinition definition = super.get(definitionName);
		WebHookService ws = (WebHookService) getService();
		
		for (WebHookSubscriber temp : definition.getSubscribers()) {
			temp.setState(WEBHOOKSUBSCRIBERSTATE.DELETE);
		}
		try {
		definition = ws.updateSubscriberListByDef(definition);
		} catch (Exception e) {
			logger.warn("Exception occured while removing the data from Database for :"+definitionName+". Caused by: "+e.getCause()+". With following message : "+e.getCause().getMessage());
		}
		return super.remove(definitionName);
	}
}
