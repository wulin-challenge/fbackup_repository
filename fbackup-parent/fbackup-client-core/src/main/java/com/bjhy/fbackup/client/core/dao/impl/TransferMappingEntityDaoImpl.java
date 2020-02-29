package com.bjhy.fbackup.client.core.dao.impl;

import com.bjhy.fbackup.client.core.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.core.domain.TransferMappingEntity;
import com.bjhy.fbackup.common.store.derby.JdbcTemplateAbstractRepository;

public class TransferMappingEntityDaoImpl extends JdbcTemplateAbstractRepository<String,TransferMappingEntity>implements TransferMappingEntityDao<String,TransferMappingEntity>{
}
