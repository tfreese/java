// Created: 06.11.2011
package de.freese.openstreetmap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Model mit allen Entitäten.
 *
 * @author Thomas Freese
 */
public class OsmModel
{
    public final Map<Long, OsmNode> nodeMap;

    public final Map<Long, OsmRelation> relationMap;

    public final Map<Long, OsmWay> wayMap;

    public OsmModel()
    {
        super();

        this.nodeMap = new HashMap<>();
        this.wayMap = new HashMap<>();
        this.relationMap = new HashMap<>();
    }

    public void clear()
    {
        this.nodeMap.clear();
        this.wayMap.clear();
        this.relationMap.clear();
    }

    public Map<Long, OsmNode> getNodeMap()
    {
        return this.nodeMap;
    }

    public Map<Long, OsmRelation> getRelationMap()
    {
        return this.relationMap;
    }

    public Map<Long, OsmWay> getWayMap()
    {
        return this.wayMap;
    }
}
