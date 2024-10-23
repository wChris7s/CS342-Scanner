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
Params' -> , Params 
         | ϵ
VarDecl -> Type Identifier VarDecl'
VarDecl' -> ; 
          | = Expression ;
StmtList -> Statement StmtList'
StmtList' -> Statement StmtList'
           | ϵ
Statement -> VarDecl 
           | IfStmt 
           | ForStmt 
           | WhileStmt 
           | ReturnStmt 
           | ExprStmt 
           | PrintStmt 
           | { StmtList }
IfStmt -> if ( Expression ) Statement IfStmt'
IfStmt' -> else Statement 
         | ϵ
ForStmt -> for ( ForInit Expression ; Expression ) Statement
ForInit -> VarDecl 
         | ExprStmt
WhileStmt -> while ( Expression ) Statement
ReturnStmt -> return Expression ; 
            | return ;
PrintStmt -> print ( ExprList ) ;
ExprStmt -> Expression ; 
          | ;
ExprList -> Expression ExprList'
ExprList' -> , ExprList 
           | ϵ

Expression -> OrExpr Expression'
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
Factor' -> ( ExprList ) 
         | ϵ
```