// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model mit allen Entitäten.
 *
 * @author Thomas Freese
 */
public class OsmModel {
    private final Map<Long, OsmNode> nodeMap;
    private final Map<Long, OsmRelation> relationMap;
    private final Map<Long, OsmWay> wayMap;

    public OsmModel() {
        super();

        nodeMap = new HashMap<>();
        wayMap = new HashMap<>();
        relationMap = new HashMap<>();
    }

    public void clear() {
        nodeMap.clear();
        wayMap.clear();
        relationMap.clear();
    }

    public Map<Long, OsmNode> getNodeMap() {
        return nodeMap;
    }

    public Map<Long, OsmRelation> getRelationMap() {
        return relationMap;
    }

    public Map<Long, OsmWay> getWayMap() {
        return wayMap;
    }
}
