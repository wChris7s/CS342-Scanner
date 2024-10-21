# Grammar
```
    Program -> Declaration Program'
   Program' -> Declaration Program'
             | ϵ
Declaration -> Function
             | VarDecl
   Function -> Type Identifier ( Params ) { StmtList }
       Type -> IntType
             | BoolType
             | CharType
             | StringType
             | VoidType
     Params -> Type Identifier Params'
    Params' -> , Params | ϵ
    VarDecl -> Type Identifier VarDecl'
   VarDecl' -> ; | = Expression ;
   StmtList -> Statement StmtList'
  StmtList' -> Statement StmtList'
             | ϵ
  Statement -> VarDecl 
             | IfStmt 
             | ForStmt 
             | ReturnStmt 
             | ExprStmt 
             | PrintStmt 
             | { StmtList }
     IfStmt -> if ( Expression ) Statement IfStmt'
    IfStmt' -> else Statement 
             | ϵ
    ForStmt -> for ( ExprStmt Expression ; ExprStmt ) Statement
 ReturnStmt -> return Expression ;
  PrintStmt -> print ( ExprList ) ;
   ExprStmt -> Expression ; 
             | ;
   ExprList -> Expression ExprList'
  ExprList' -> , ExprList 
             | ϵ

 Expression -> Identifier Expression' 
             | OrExpr
Expression' -> = Expression 
             | ϵ
     OrExpr -> AndExpr OrExpr'
    OrExpr' -> || AndExpr OrExpr'
             | ϵ
    AndExpr -> EqExpr AndExpr'
   AndExpr' -> && EqExpr AndExpr'
             | ϵ
     EqExpr -> RelExpr EqExpr'
    EqExpr' -> == RelExpr EqExpr'
             | != RelExpr EqExpr'
             | ϵ
    RelExpr -> Expr RelExpr'
   RelExpr' -> < Expr RelExpr'
             | > Expr RelExpr'
             | <= Expr RelExpr'
             | >= Expr RelExpr'
             | ϵ
       Expr -> Term Expr'
      Expr' -> + Term Expr'
             | - Term Expr'
             | ϵ
       Term -> Unary Term'
      Term' -> * Unary Term'
             | / Unary Term'
             | % Unary Term'
             | ϵ
      Unary -> ! Unary
             | - Unary
             | Factor
     Factor -> Identifier Factor'
             | IntLiteral
             | CharLiteral
             | StringLiteral
             | BoolLiteral
             | ( Expression )
    Factor' -> ( ExprList ) | ϵ
```