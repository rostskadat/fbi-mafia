package com.stratio.fbi.mafia.model;

import java.util.Iterator;

/**
 * @see https://github.com/gt4dev/yet-another-tree-structure
 */
public class MafiaCellIterator implements Iterator<MafiaCell> {

    enum ProcessStages {
        ProcessParent, ProcessChildCurNode, ProcessChildSubNode
    }

    private MafiaCell treeNode;

    public MafiaCellIterator(MafiaCell treeNode) {
        this.treeNode = treeNode;
        this.doNext = ProcessStages.ProcessParent;
        this.childrenCurNodeIter = treeNode.getSubordinates().iterator();
    }

    private ProcessStages doNext;
    private MafiaCell next;
    private Iterator<MafiaCell> childrenCurNodeIter;
    private Iterator<MafiaCell> childrenSubNodeIter;

    @Override
    public boolean hasNext() {

        if (this.doNext == ProcessStages.ProcessParent) {
            this.next = this.treeNode;
            this.doNext = ProcessStages.ProcessChildCurNode;
            return true;
        }

        if (this.doNext == ProcessStages.ProcessChildCurNode) {
            if (childrenCurNodeIter.hasNext()) {
                MafiaCell childDirect = childrenCurNodeIter.next();
                childrenSubNodeIter = childDirect.iterator();
                this.doNext = ProcessStages.ProcessChildSubNode;
                return hasNext();
            }

            else {
                this.doNext = null;
                return false;
            }
        }

        if (this.doNext == ProcessStages.ProcessChildSubNode) {
            if (childrenSubNodeIter.hasNext()) {
                this.next = childrenSubNodeIter.next();
                return true;
            } else {
                this.next = null;
                this.doNext = ProcessStages.ProcessChildCurNode;
                return hasNext();
            }
        }

        return false;
    }

    @Override
    public MafiaCell next() {
        return this.next;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}