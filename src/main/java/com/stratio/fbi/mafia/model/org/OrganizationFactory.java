package com.stratio.fbi.mafia.model.org;

import com.stratio.fbi.mafia.model.Mafioso;
import com.stratio.fbi.mafia.model.org.flat.PathListMafiaOrganization;
import com.stratio.fbi.mafia.model.org.flat.RelationListMafiaOrganization;
import com.stratio.fbi.mafia.model.org.tree.TreeMafiaOrganization;

/**
 * This class is in charge of creating all the known type of {@link MafiaOrganization}
 * 
 * @author rostskadat
 *
 */
public class OrganizationFactory {

    private OrganizationFactory() {
        // NA
    }

    /**
     * Create a {@link TreeMafiaOrganization} whose cupula is formed by the {@code godfather}
     * 
     * @param godfather
     *            the Boss
     * @param isDeep
     *            Indicates whether the count should be deep or not
     * @return the {@link MafiaOrganization}
     */
    public static MafiaOrganization createTree(Mafioso godfather, boolean isDeep) {
        MafiaOrganization organization = new TreeMafiaOrganization();
        organization.setCupula(godfather);
        organization.setDeep(isDeep);
        return organization;
    }

    /**
     * Create a {@link PathListMafiaOrganization} whose cupula is formed by the {@code godfather}
     * 
     * @param godfather
     *            the Boss
     * @param isDeep
     *            Indicates whether the count should be deep or not
     * @return the {@link MafiaOrganization}
     */
    public static MafiaOrganization createPathList(Mafioso godfather, boolean isDeep) {
        MafiaOrganization organization = new PathListMafiaOrganization();
        organization.setCupula(godfather);
        organization.setDeep(isDeep);
        return organization;
    }

    /**
     * Create a {@link RelationListMafiaOrganization} whose cupula is formed by the {@code godfather}
     * 
     * @param godfather
     *            the Boss
     * @param isDeep
     *            Indicates whether the count should be deep or not
     * @return the {@link MafiaOrganization}
     */
    public static MafiaOrganization createRelationList(Mafioso godfather, boolean isDeep) {
        MafiaOrganization organization = new RelationListMafiaOrganization();
        organization.setCupula(godfather);
        organization.setDeep(isDeep);
        return organization;
    }
}
