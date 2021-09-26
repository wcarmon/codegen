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
    db  *sql.DB
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Delete${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) error {
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
    return err
  }
  defer tx.Rollback()

  stmt, err := tx.PrepareContext(ctx, DELETE__${entity.name.upperSnake})
  if err != nil {
    return err
  }
  defer stmt.Close()

  res, err := stmt.ExecContext(ctx, ${entity.golangView.commaSeparatedIdFields})
  if err != nil {
    return err
  }

  rowCnt, err := res.RowsAffected()
  if err != nil {
    return err
  }
  //TODO: add affected row count to trace

  err = tx.Commit()
  if err != nil {
    return err
  }

  return nil
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) ${entity.name.upperCamel}Exists(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (bool, error) {
  //TODO: more here
  //TODO: use QueryRowContext
  // 	err = stmt.QueryRowContext(ctx, ${entity.golangView.commaSeparatedIdFields}).
  //  Scan(&username)
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) FindById${entity.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}) (*${entity.name.upperCamel}, error) {
  stmt, err := dao.db.PrepareContext(ctx, FIND_BY_ID__${entity.name.upperSnake})
  if err != nil {
    return nil, err
  }
  defer stmt.Close()

  var output ${entity.name.upperCamel}

  err = stmt.QueryRowContext(
    ctx, ${entity.golangView.commaSeparatedIdFields}).
  Scan(
    //TODO: scan for each variable name
    &output.foo,)

  switch {
  case err == sql.ErrNoRows:
    return nil, nil

  case err != nil:
    return nil, err

  default:
    return &output, nil
  }
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Create${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
  //TODO: more here
  //TODO: use transaction
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) Update${entity.name.upperCamel}(ctx context.Context, entity ${entity.name.upperCamel}) error {
  //TODO: more here
  //TODO: use transaction
}

func (dao *${entity.name.upperCamel}PostgreSQLDAO) List${entity.name.upperCamel}(ctx context.Context) ([]${entity.name.upperCamel}, error) {
  //TODO: more here
  //TODO: use db.QueryContext (not QueryRowContext)

  for rows.Next() {
    // ... append(...) ...
  }
  if err = rows.Err(); err != nil {
    // handle the error here
  }
}

<#list entity.nonIdFields as field>
func (dao *${entity.name.upperCamel}PostgreSQLDAO) Set${field.name.upperCamel}(ctx context.Context, ${entity.golangView.methodArgsForIdFields()}, ${field.name.lowerCamel} ${field.golangView.typeLiteral}) error {
  //TODO: more here
  //TODO: use transaction
}
</#list>

</#list>
