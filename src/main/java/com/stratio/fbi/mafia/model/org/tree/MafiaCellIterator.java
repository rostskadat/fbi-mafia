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
    private Integer maxDepth;

    public MafiaCellIterator(MafiaCell start) {
        this(start, true, -1);
	}

    public MafiaCellIterator(MafiaCell start, boolean includeStartNode, int maxDepth) {
        this.stage = includeStartNode ? ProcessStages.PARENT : ProcessStages.CHILDREN_CURRENT_NODE;
        this.current = start;
        this.childrenCurNodeIter = current.getSubordinates().iterator();
        this.maxDepth = maxDepth;
	}

	@Override
	public boolean hasNext() {

        if (maxDepth != -1 && current.getLevel() > maxDepth) {
            return false;
        }
	    
        if (stage == ProcessStages.PARENT) {
            next = current;
            stage = ProcessStages.CHILDREN_CURRENT_NODE;
			return true;
		}

        if (stage == ProcessStages.CHILDREN_CURRENT_NODE) {
            if (childrenCurNodeIter.hasNext()) {
				MafiaCell childDirect = childrenCurNodeIter.next();
                // XXX:
                childrenSubNodeIter = childDirect.iterator(true, maxDepth);
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