package com.bjhy.fbackup.client.dao.impl;

import com.bjhy.fbackup.client.dao.TransferMappingEntityDao;
import com.bjhy.fbackup.client.domain.TransferMappingEntity;
import com.bjhy.fbackup.common.store.derby.JdbcTemplateAbstractRepository;

public class TransferMappingEntityDaoImpl extends JdbcTemplateAbstractRepository<String,TransferMappingEntity>implements TransferMappingEntityDao<String,TransferMappingEntity>{
}
