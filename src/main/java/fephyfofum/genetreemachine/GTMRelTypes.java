package fephyfofum.genetreemachine;

import org.neo4j.graphdb.RelationshipType;

public enum GTMRelTypes implements RelationshipType{
	DUPLICATIONFROM; //this will point to a node which is inferred to be a paralog or duplication, from node to parent
}
