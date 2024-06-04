package Languages.C;

import Languages.Code;
import Languages.Java.Java;
import Transpiler.AbstractSyntaxTree;
import Transpiler.RuleType;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class C extends Code {

    public C(String code) {
        super(code);
    }

    public C(AbstractSyntaxTree ast) {
        super(ast);
    }

    @Override
    public AbstractSyntaxTree parseToAST() {
        /**
         * This function parses the given program code with C Parser.
         * @return  Parsed AST (Abstract Syntax Tree) of the given Program.
         */
        CLexer lexer = new CLexer(new StringReader(this.code));
        CParser parser = new CParser(lexer);
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
         * @param tree: The AST (Abstract Syntax Tree) of the program we want to generate the code for.
         * @return  The generated C program code for the given AST.
         */
        // TODO: Generate the Code from the given AST
        StringBuilder code = new StringBuilder();
        AbstractSyntaxTree root = this.ast;
        if(root == null) return code.toString();
        switch (root.getType()) {
            case PROGRAM -> {
                code.append("void ");
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(" () {\n");
                code.append(new C(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case STATEMENTS, OPTIONS -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case ASSIGNMENTS -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(", ");
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case FOLLOW_STATEMENTS -> {
                switch (root.getSubType()) {
                    case MULTI -> {
                        code.append(" {\n");
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(" }\n");
                    }
                    case SINGLE -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                    case EMPTY -> {
                        code.append("{}");
                    }
                }
            }
            case STATEMENT -> {
                switch (root.getSubType()) {
                    case DECLARE, ASSIGNMENTS -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(";\n");
                    }
                    case BREAK -> {
                        code.append("break;\n");
                    }
                    case CONTINUE -> {
                        code.append("continue;\n");
                    }
                    case PRINT -> {
                        code.append("cout << ");
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(";\n");
                    }
                    case IF, SWITCH, WHILE -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case PRINT -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(" << ");
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(" << ");
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(" << ");
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case DECLARATION -> {
                code.append("int ");
                code.append(new C(root.getChildren().get(0)).generateCode());
            }
            case ASSIGNMENT -> {
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(" = ");
                code.append(new C(root.getChildren().get(1)).generateCode());
            }
            case IF -> {
                code.append("if (");
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(")");
                code.append(new C(root.getChildren().get(1)).generateCode());
                code.append("else ");
                code.append(new C(root.getChildren().get(2)).generateCode());
            }
            case SWITCH -> {
                code.append("switch (");
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(") {\n");
                code.append(new C(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case CASES -> {

                if (root.getSubType().equals(RuleType.DEFAULT)) {
                    code.append("default: ");
                    code.append(new C(root.getChildren().get(0)).generateCode());
                } else {
                    ArrayList<Integer> cases = new ArrayList<>();
                    char[] Cases = new C(root.getChildren().get(0)).generateCode().toCharArray();
                    for (char c : Cases) {
                        if (Pattern.matches("[0-9]", String.valueOf(c))) {
                            cases.add(Integer.parseInt(String.valueOf(c)));
                        }
                    }
                    for (int i : cases) {
                        code.append("case ");
                        code.append(i);
                        code.append(": ");
                        AbstractSyntaxTree ast = root.getChildren().get(1);
                        ast.setSubType(RuleType.SINGLE);
                        code.append(new C(ast).generateCode());
                    }
                    code.append(new C(root.getChildren().get(2)).generateCode());
                }
            }
            case WHILE -> {
                code.append("while (");
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(") ");
                code.append(new C(root.getChildren().get(1)).generateCode());
            }
            case DISJUNCTION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(" || ");
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case CONJUNCTION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(" && ");
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case INVERSION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append("!");
                    code.append(new C(root.getChildren().get(0)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case COMPARISON, PRIMARY -> {
                code.append(new C(root.getChildren().get(0)).generateCode());
            }
            case EQ -> {
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(" == ");
                code.append(new C(root.getChildren().get(1)).generateCode());
            }
            case LT -> {
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(" < ");
                code.append(new C(root.getChildren().get(1)).generateCode());
            }
            case GT -> {
                code.append(new C(root.getChildren().get(0)).generateCode());
                code.append(" > ");
                code.append(new C(root.getChildren().get(1)).generateCode());
            }
            case SUM -> {
                switch (root.getSubType()) {
                    case ADD -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(" + ");
                        code.append(new C(root.getChildren().get(1)).generateCode());
                    }
                    case SUB -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(" - ");
                        code.append(new C(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case TERM -> {
                switch (root.getSubType()) {
                    case TIMES -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(" * ");
                        code.append(new C(root.getChildren().get(1)).generateCode());
                    }
                    case DIVIDES -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(" / ");
                        code.append(new C(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case MODULO -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                    code.append(" % ");
                    code.append(new C(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new C(root.getChildren().get(0)).generateCode());
                }
            }
            case FACTOR -> {
                switch (root.getSubType()) {
                    case POSITIVE -> {
                        code.append("+");
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                    case NEGATIVE -> {
                        code.append("-");
                        code.append(new C(root.getChildren().get(0)).generateCode());
                    }
                    case PAR -> {
                        code.append("(");
                        code.append(new C(root.getChildren().get(0)).generateCode());
                        code.append(")");
                    }
                    case DEFAULT -> {
                        code.append(new C(root.getChildren().get(0)).generateCode());
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
