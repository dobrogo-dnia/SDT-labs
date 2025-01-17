package visitor;

import model.File;
import model.Session;
import model.User;

public interface Visitor {
    void visit(User user);
    void visit(File file);
    void visit(Session session);
}
