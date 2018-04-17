package com.bjhy.fbackup.client.dao.impl;

import com.bjhy.fbackup.client.dao.FileTransferEntityDao;
import com.bjhy.fbackup.client.domain.FileTransferEntity;
import com.bjhy.fbackup.common.store.derby.JdbcTemplateAbstractRepository;

public class FileTransferEntityDaoImpl extends JdbcTemplateAbstractRepository<String,FileTransferEntity> implements FileTransferEntityDao<String,FileTransferEntity>{
}
