package ${request.packageName.value}
${request.golangView.templateDebugInfo}

${request.golangView.serializeImports(request.extraGolangImports)}

// -------------------------------------------------------------------
// Prepared Statement DAOs
// Compatible with PostgreSQL
// (Placeholders like $1, $2, $3, ...)
//
// See https://golang.org/doc/database/prepared-statements
// -------------------------------------------------------------------

<#list entities as entity>
type ${entity.name.upperCamel}PostgreSQLDAO struct {
    db    *sql.DB
    now   func() time.Time
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Delete${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (deleted bool, err error) {
<#--
TODO: which is best?
  LevelReadUncommitted
  LevelReadCommitted
  LevelWriteCommitted
  LevelRepeatableRead
  LevelSnapshot
  LevelSerializable
  LevelLinearizable
-->

  tx, err := dao.db.BeginTx(ctx, &sql.TxOptions{Isolation: sql.LevelSerializable})
  if err != nil {
    return false, err
  }
  defer tx.Rollback()

  stmt, err := tx.PrepareContext(ctx, DELETE__${entity.name.upperSnake})
  if err != nil {
    return false, err
  }
  defer stmt.Close()

  res, err := stmt.ExecContext(ctx, ${entity.golangView.commaSeparatedIdFields})
  if err != nil {
    return false, err
  }

  rowCnt, err := res.RowsAffected()
  if err != nil {
    return false, err
  }
  //TODO: add affected row count to trace (in ctx)

  err = tx.Commit()
  if err != nil {
    return false, err
  }

  return rowCnt == 1, nil
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) ${entity.name.upperCamel}Exists(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (bool, error) {
  stmt, err := dao.db.PrepareContext(ctx, ROW_EXISTS__${entity.name.upperSnake})
  if err != nil {
    return false, err
  }
  defer stmt.Close()

  var count int32
  err = stmt.
    QueryRowContext(ctx, ${entity.golangView.commaSeparatedIdFields}).
    Scan(&count)
  if err != nil {
    return false, err
  }

  return count == 1, nil
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) FindById${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (*${entity.name.upperCamel}, error) {
  stmt, err := dao.db.PrepareContext(ctx, FIND_BY_ID__${entity.name.upperSnake})
  if err != nil {
    return nil, err
  }
  defer stmt.Close()

  var entity ${entity.name.upperCamel}
  err = stmt.
    QueryRowContext(ctx, ${entity.golangView.commaSeparatedIdFields}).
    Scan(
      ${entity.golangView.commaSeparatedFieldsForQueryScan("entity")}
    )

  switch {
  case err == sql.ErrNoRows:
    return nil, nil

  case err != nil:
    return nil, err

  default:
    return &entity, nil
  }
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Create${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
  tx, err := dao.db.BeginTx(ctx, &sql.TxOptions{Isolation: sql.LevelSerializable})
  if err != nil {
    return err
  }
  defer tx.Rollback()

  stmt, err := tx.PrepareContext(ctx, INSERT__${entity.name.upperSnake})
  if err != nil {
    return err
  }
  defer stmt.Close()

  res, err := stmt.ExecContext(
    ctx,
    ${entity.golangView.commaSeparatedFieldReadsForInsert("entity")}
  )
  if err != nil {
    return err
  }

  rowCnt, err := res.RowsAffected()
  if err != nil {
    return err
  }
  //TODO: add affected row count to trace (in ctx)

  err = tx.Commit()
  if err != nil {
    return err
  }

  return nil
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Update${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
  tx, err := dao.db.BeginTx(ctx, &sql.TxOptions{Isolation: sql.LevelSerializable})
  if err != nil {
    return err
  }
  defer tx.Rollback()

  stmt, err := tx.PrepareContext(ctx, UPDATE__${entity.name.upperSnake})
  if err != nil {
    return err
  }
  defer stmt.Close()

  res, err := stmt.ExecContext(
    ctx,
    ${entity.golangView.commaSeparatedFieldReadsForUpdate("entity")}
  )
  if err != nil {
    return err
  }

  rowCnt, err := res.RowsAffected()
  if err != nil {
    return err
  }
  //TODO: add affected row count to trace (in ctx)

  err = tx.Commit()
  if err != nil {
    return err
  }

  return nil
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) List${entity.name.upperCamel}(ctx context.Context) ([]${entity.name.upperCamel}, error) {
  stmt, err := dao.db.PrepareContext(ctx, SELECT_ALL__${entity.name.upperSnake} )
  if err != nil {
    return nil, err
  }
  defer stmt.Close()

  rows, err := stmt.QueryContext(ctx)
  switch {
  case err == sql.ErrNoRows:
    return nil, nil
  case err != nil:
    return nil, err
  }
  defer rows.Close()

  var entities []${entity.name.upperCamel}

  for rows.Next() {
    var entity ${entity.name.upperCamel}
    err = rows.Scan(
      ${entity.golangView.commaSeparatedFieldsForQueryScan("entity")}
    )
    switch {
    case err == sql.ErrNoRows:
      return entities, nil
    case err != nil:
      return entities, err
    }

    entities = append(entities, entity)
  }

  err = rows.Close()
  if err != nil {
    return entities, err
  }

  err = rows.Err()
  if err != nil {
    return entities, err
  }

  return entities, nil
}

<#list entity.nonIdFields as field>
func (dao *${entity.name.upperCamel}PostgreSQLDAO) Set${field.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}, ${field.name.lowerCamel} ${field.golangView.typeLiteral}) error {
  tx, err := dao.db.BeginTx(ctx, &sql.TxOptions{Isolation: sql.LevelSerializable})
  if err != nil {
    return err
  }
  defer tx.Rollback()

  stmt, err := tx.PrepareContext(ctx, PATCH__${entity.name.upperSnake}__${field.name.upperSnake})
  if err != nil {
    return err
  }
  defer stmt.Close()

  res, err := stmt.ExecContext(
    ctx,
    ${field.name.lowerCamel},
    ${entity.golangView.renderUpdateTimestampField(
      field,
      "dao.now()"
      ",\n")}<#--
 -->${entity.golangView.commaSeparatedIdFields})
  if err != nil {
    return err
  }

  rowCnt, err := res.RowsAffected()
  if err != nil {
    return err
  }
  //TODO: add affected row count to trace (in ctx)

  err = tx.Commit()
  if err != nil {
    return err
  }

  return nil
}
</#list>

</#list>
