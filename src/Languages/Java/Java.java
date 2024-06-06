package Languages.Java;

import Languages.Code;
import Transpiler.AbstractSyntaxTree;
import Transpiler.RuleType;

import java.io.StringReader;

public class Java extends Code {
    public Java(String code) {
        super(code);
    }

    public Java(AbstractSyntaxTree ast) {
        super(ast);
    }

    @Override
    public AbstractSyntaxTree parseToAST() {
        /**
         * This function parses the given program code with Java Parser.
         * @return Parsed AST (Abstract Syntax Tree) of the given Program.
         */
        JavaLexer lexer = new JavaLexer(new StringReader(this.code));
        JavaParser parser = new JavaParser(lexer);
        try {
            Object result = parser.parse().value;
            this.ast = (AbstractSyntaxTree) result;
            return this.ast;
        } catch (Exception e) {
            System.err.println("Parser error: " + e.getMessage());
            this.ast = null;
            return null;

        }
    }

    @Override
    public String generateCode() {
        /**
         * This function reverses the parsing process to generate a string code for the given AST.
         * @return The generated Java program code for the given AST.
         */
        StringBuilder code = new StringBuilder();
        AbstractSyntaxTree root = this.ast;
        if (root == null) return code.toString();
        switch (root.getType()) {
            case PROGRAM -> {
                code.append("public static void ");
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append("() {\n");
                code.append(new Java(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case STATEMENTS, OPTIONS -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                    code.append(new Java(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case ASSIGNMENTS -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                    code.append(", ");
                    code.append(new Java(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case FOLLOW_STATEMENTS -> {
                switch (root.getSubType()) {
                    case MULTI -> {
                        code.append("{\n");
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append("}\n");
                    }
                    case SINGLE -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                    case EMPTY -> {
                        code.append("{}");
                    }
                }
            }
            case STATEMENT -> {
                switch (root.getSubType()) {
                    case DECLARE, ASSIGNMENTS -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(";\n");
                    }
                    case BREAK -> {
                        code.append("break;\n");
                    }
                    case CONTINUE -> {
                        code.append("continue;\n");
                    }
                    case PRINT -> {
                        code.append("System.out.println(");
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(");\n");
                    }
                    case IF, SWITCH, WHILE -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case PRINT -> {
                code.append(new Java(root.getChildren().get(0)).generateCode());
            }
            case DECLARATION -> {
                code.append("int ");
                code.append(new Java(root.getChildren().get(0)).generateCode());
            }
            case ASSIGNMENT -> {
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(" = ");
                code.append(new Java(root.getChildren().get(1)).generateCode());
            }
            case IF -> {
                code.append("if (");
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(")");
                code.append(new Java(root.getChildren().get(1)).generateCode());
                code.append("else ");
                code.append(new Java(root.getChildren().get(2)).generateCode());
            }
            case SWITCH -> {
                code.append("switch (");
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(") {\n");
                code.append(new Java(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case CASES -> {

                if (root.getSubType().equals(RuleType.DEFAULT)) {
                    code.append("default");
                    code.append(" : ");
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                } else {
                    char[] cases = new Java(root.getChildren().get(0)).generateCode().toCharArray();
                    for (char c : cases) {
                        code.append("case ");
                        code.append(c);
                        code.append(": ");
                        AbstractSyntaxTree ast = root.getChildren().get(1);
                        ast.setSubType(RuleType.SINGLE);
                        code.append(new Java(ast).generateCode());
                    }
                    code.append(new Java(root.getChildren().get(2)).generateCode());
                }
            }
            case WHILE -> {
                code.append("while (");
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(") ");
                code.append(new Java(root.getChildren().get(1)).generateCode());
            }
            case DISJUNCTION -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                    code.append(" || ");
                    code.append(new Java(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case CONJUNCTION -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                    code.append(" && ");
                    code.append(new Java(root.getChildren().get(1)).generateCode());
                } else {

                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case INVERSION -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append("!");
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                } else {

                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case COMPARISON, PRIMARY -> {

                code.append(new Java(root.getChildren().get(0)).generateCode());
            }
            case EQ -> {
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(" == ");
                code.append(new Java(root.getChildren().get(1)).generateCode());
            }
            case LT -> {
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(" < ");
                code.append(new Java(root.getChildren().get(1)).generateCode());
            }
            case GT -> {
                code.append(new Java(root.getChildren().get(0)).generateCode());
                code.append(" > ");
                code.append(new Java(root.getChildren().get(1)).generateCode());
            }
            case SUM -> {
                switch (root.getSubType()) {
                    case ADD -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(" + ");
                        code.append(new Java(root.getChildren().get(1)).generateCode());
                    }
                    case SUB -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(" - ");
                        code.append(new Java(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case TERM -> {
                switch (root.getSubType()) {
                    case TIMES -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(" * ");
                        code.append(new Java(root.getChildren().get(1)).generateCode());
                    }
                    case DIVIDES -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(" / ");
                        code.append(new Java(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case MODULO -> {
                if (root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                    code.append(" % ");
                    code.append(new Java(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Java(root.getChildren().get(0)).generateCode());
                }
            }
            case FACTOR -> {
                switch (root.getSubType()) {
                    case POSITIVE -> {
                        code.append("+");
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                    case NEGATIVE -> {
                        code.append("-");
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                    case PAR -> {
                        code.append("(");
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                        code.append(")");
                    }
                    case DEFAULT -> {
                        code.append(new Java(root.getChildren().get(0)).generateCode());
                    }
                }
            }

            case ID, NUM -> {
                code.append(root.getLexeme());
            }


        }

        return code.toString();
    }
}
