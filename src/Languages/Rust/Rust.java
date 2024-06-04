package Languages.Rust;

import Languages.Code;
import Languages.Java.Java;
import Transpiler.AbstractSyntaxTree;
import Transpiler.RuleType;

import java.io.StringReader;

public class Rust extends Code {

    public Rust(String code) {
        super(code);
    }

    public Rust(AbstractSyntaxTree ast) {
        super(ast);
    }

    @Override
    public AbstractSyntaxTree parseToAST() {
        /**
         * This function parses the given program code with Rust Parser.
         * @return  Parsed AST (Abstract Syntax Tree) of the given Program.
         */
        RustLexer lexer = new RustLexer(new StringReader(this.code));
        RustParser parser = new RustParser(lexer);
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
         * @return  The generated Rust program code for the given AST.
         */
        StringBuilder code = new StringBuilder();
        AbstractSyntaxTree root = this.ast;
        if (root == null) return code.toString();
        switch (root.getType()) {
            case PROGRAM -> {
                code.append("fn ");
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append("() {\n");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case STATEMENTS -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case FOLLOW_STATEMENTS -> {
                switch (root.getSubType()) {
                    case MULTI -> {
                        code.append("{\n");
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append("}\n");
                    }
                    case SINGLE -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                    case EMPTY -> {
                        code.append("{ }\n");
                    }
                }
            }
            case STATEMENT -> {
                switch (root.getSubType()) {
                    case DECLARE , ASSIGNMENTS -> {

                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(";\n");
                    }
                    case BREAK -> {
                        code.append("break;\n");
                    }
                    case CONTINUE -> {
                        code.append("continue;\n");
                    }
                    case PRINT -> {
                        code.append("println!(");
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(");\n");
                    }
                    case IF, SWITCH, WHILE -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case PRINT -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
            }
            case ASSIGNMENTS -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(";\n");
                    code.append("let ");
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {

                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case DECLARATION -> {
                if(root.getChildren().get(0).getSubType().equals(RuleType.MULTI)) {
                    code.append("let ");
                    code.append(new Rust(root.getChildren().get(0)).generateCode());

                } else {
                    code.append("let ");
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case ASSIGNMENT -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(" = ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
            }
            case IF -> {
                code.append("if (");
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(") ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
                code.append("else ");
                code.append(new Rust(root.getChildren().get(2)).generateCode());
            }
            case SWITCH -> {
                code.append("match ");
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(" {\n");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
                code.append("}\n");
            }
            case CASES -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                   code.append(new Rust(root.getChildren().get(0)).generateCode());
                   code.append(" => ");
                   code.append(new Rust(root.getChildren().get(1)).generateCode());
                   code.append(",\n");
                   code.append(new Rust(root.getChildren().get(2)).generateCode());
                } else {
                    code.append(" _ => ");
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case OPTIONS -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(" | ");
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case WHILE -> {
                code.append("while (");
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(") ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
            }
            case DISJUNCTION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(" || ");
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case CONJUNCTION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(" && ");
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case INVERSION -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append("!");
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case COMPARISON, PRIMARY -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
            }
            case EQ -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(" == ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
            }
            case LT -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(" < ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
            }
            case GT -> {
                code.append(new Rust(root.getChildren().get(0)).generateCode());
                code.append(" > ");
                code.append(new Rust(root.getChildren().get(1)).generateCode());
            }
            case SUM -> {
                switch (root.getSubType()) {
                    case ADD -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(" + ");
                        code.append(new Rust(root.getChildren().get(1)).generateCode());
                    }
                    case SUB -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(" - ");
                        code.append(new Rust(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case TERM -> {
                switch (root.getSubType()) {
                    case TIMES -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(" * ");
                        code.append(new Rust(root.getChildren().get(1)).generateCode());
                    }
                    case DIVIDES -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(" / ");
                        code.append(new Rust(root.getChildren().get(1)).generateCode());
                    }
                    case DEFAULT -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                }
            }
            case MODULO -> {
                if(root.getSubType().equals(RuleType.MULTI)) {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                    code.append(" % ");
                    code.append(new Rust(root.getChildren().get(1)).generateCode());
                } else {
                    code.append(new Rust(root.getChildren().get(0)).generateCode());
                }
            }
            case FACTOR -> {
                switch (root.getSubType()) {
                    case POSITIVE -> {
                        code.append("+");
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                    case NEGATIVE -> {
                        code.append("-");
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                    }
                    case PAR -> {
                        code.append("(");
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
                        code.append(")");
                    }
                    case DEFAULT -> {
                        code.append(new Rust(root.getChildren().get(0)).generateCode());
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
