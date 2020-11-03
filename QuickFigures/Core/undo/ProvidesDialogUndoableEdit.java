package undo;

import javax.swing.undo.AbstractUndoableEdit;

public interface ProvidesDialogUndoableEdit {
	AbstractUndoableEdit provideUndoForDialog();
}
