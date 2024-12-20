package userMemento;

import java.util.*;

public class UserCaretaker {
    private final List<UserMemento> mementoList = new ArrayList<>();

    public void saveNewMemento(UserMemento userMemento) {
        mementoList.add(userMemento);
    }

    public UserMemento getLastSavedMemento() {
        if(mementoList.isEmpty()) return null;
        int lastIndex = mementoList.size() - 1;
        UserMemento lastMemento = mementoList.get(lastIndex);
        mementoList.remove(lastIndex);
        return lastMemento;
    }
}