package com.stratio.fbi.mafia.model.org.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see https://github.com/gt4dev/yet-another-tree-structure
 */
public class MafiaCellIterator implements Iterator<MafiaCell> {

	enum ProcessStages {
		PARENT, CHILDREN_CURRENT_NODE, CHILDREN_SUB_NODE
	}

    private ProcessStages stage;
	private MafiaCell current;
    private MafiaCell next;
    private Iterator<MafiaCell> childrenCurNodeIter;
    private Iterator<MafiaCell> childrenSubNodeIter;
    private boolean goDeep;

    public MafiaCellIterator(MafiaCell start) {
        this(start, true, true);
	}

    public MafiaCellIterator(MafiaCell start, boolean includeStartNode, boolean goDeep) {
        this.stage = includeStartNode ? ProcessStages.PARENT : ProcessStages.CHILDREN_CURRENT_NODE;
        this.current = start;
		this.childrenCurNodeIter = current.getSubordinates().iterator();
        this.goDeep = goDeep;
	}

	@Override
	public boolean hasNext() {

        if (stage == ProcessStages.PARENT) {
            next = current;
            stage = ProcessStages.CHILDREN_CURRENT_NODE;
			return true;
		}

        // TODO: handle the goDeep flag...

        if (stage == ProcessStages.CHILDREN_CURRENT_NODE) {
            if (childrenCurNodeIter.hasNext()) {
				MafiaCell childDirect = childrenCurNodeIter.next();
				childrenSubNodeIter = childDirect.iterator();
                stage = ProcessStages.CHILDREN_SUB_NODE;
				return hasNext();
            } else {
                stage = null;
				return false;
			}
		}

        if (stage == ProcessStages.CHILDREN_SUB_NODE) {
			if (childrenSubNodeIter.hasNext()) {
                next = childrenSubNodeIter.next();
				return true;
			} else {
                next = null;
                stage = ProcessStages.CHILDREN_CURRENT_NODE;
				return hasNext();
			}
		}

		return false;
	}

	@Override
	public MafiaCell next() {
        if (next == null) {
            throw new NoSuchElementException();
        }
        return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}