package com.bjhy.fbackup.client.core.dao.impl;

import com.bjhy.fbackup.client.core.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.core.domain.FileTransferEntity;
import com.bjhy.fbackup.common.store.derby.JdbcTemplateAbstractRepository;

public class FileTransferEntityDaoImpl extends JdbcTemplateAbstractRepository<String,FileTransferEntity> implements FileTransferEntityDao<String,FileTransferEntity>{
}
