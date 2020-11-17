/*
 * Copyright (C) 2013 INFORMATION SERVICES INTERNATIONAL - DENTSU, LTD. All Rights Reserved.
 * 
 * Unless you have purchased a commercial license,
 * the following license terms apply:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.iplass.mtp.impl.properties.extend.select;

import java.util.Arrays;
import org.iplass.mtp.ManagerLocator;
import org.iplass.mtp.entity.SelectValue;
import org.iplass.mtp.entity.definition.EntityDefinition;
import org.iplass.mtp.entity.definition.EntityDefinitionManager;
import org.iplass.mtp.entity.definition.properties.SelectProperty;
import org.iplass.mtp.entity.definition.properties.selectvalue.SelectValueDefinition;
import org.iplass.mtp.entity.definition.properties.selectvalue.SelectValueDefinitionManager;
import org.iplass.mtp.impl.definition.AbstractTypedDefinitionManager;
import org.iplass.mtp.impl.definition.TypedMetaDataService;
import org.iplass.mtp.impl.entity.EntityDefinitionManagerImpl;
import org.iplass.mtp.impl.metadata.RootMetaData;
import org.iplass.mtp.spi.ServiceRegistry;

public class SelectValueDefinitionManagerImpl extends AbstractTypedDefinitionManager<SelectValueDefinition> implements
		SelectValueDefinitionManager {

	private SelectValueService service;

	public SelectValueDefinitionManagerImpl() {
		service = ServiceRegistry.getRegistry().getService(SelectValueService.class);
	}

	@Override
	public Class<SelectValueDefinition> getDefinitionType() {
		return SelectValueDefinition.class;
	}

	@Override
	protected RootMetaData newInstance(SelectValueDefinition definition) {
		return new MetaSelectValue();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected TypedMetaDataService getService() {
		return service;
	}
	
	@Override
	public SelectValue getSelectValue(String entityName, String propertyName, String value) {
		
		EntityDefinitionManager entityDefinitionManager = ManagerLocator.manager(EntityDefinitionManager.class);
		EntityDefinition entityDefinition = entityDefinitionManager.get(entityName);

		if(entityDefinition == null || value == null) {
			return null;
		}
		
		SelectProperty selectProperty = entityDefinition.getPropertyList().stream()
				.filter(p -> p instanceof SelectProperty)
				.filter(p -> p.getName().equals(propertyName))
				.map(p -> (SelectProperty)p)
				.findFirst().orElse(new SelectProperty());
		
		return selectProperty.getSelectValue(value) == null 
				? new SelectValue(value) 
				: selectProperty.getSelectValue(value);
	}
}
