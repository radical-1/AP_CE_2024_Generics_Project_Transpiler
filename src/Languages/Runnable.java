package Languages;

import Transpiler.AbstractSyntaxTree;

public interface Runnable {
    public AbstractSyntaxTree parseToAST();
    public String generateCode();
}
