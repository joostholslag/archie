package com.nedap.archie.rules.evaluation;

import com.nedap.archie.ArchieLanguageConfiguration;
import com.nedap.archie.adlparser.ADLParser;
import com.nedap.archie.adlparser.modelconstraints.RMConstraintImposer;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.creation.RMObjectCreator;
import com.nedap.archie.rm.archetyped.Locatable;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.datastructures.Element;
import com.nedap.archie.rm.datastructures.ItemTree;
import com.nedap.archie.rm.datavalues.DvBoolean;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rminfo.ArchieRMInfoLookup;
import com.nedap.archie.testutil.TestUtil;
import com.nedap.archie.xml.JAXBUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openehr.referencemodels.BuiltinReferenceModels;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pieter.bos on 04/04/2017.
 */
public class FixableAssertionsCheckerTest {

    private ADLParser parser;
    private Archetype archetype;

    private TestUtil testUtil;
    private RMObjectCreator rmObjectCreator;

    @Before
    public void setup() {
        testUtil = new TestUtil();
        rmObjectCreator = new RMObjectCreator(ArchieRMInfoLookup.getInstance());
        parser = new ADLParser(BuiltinReferenceModels.getMetaModels());
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage("en");
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage("en");
    }

    @After
    public void tearDown() throws Exception {
        ArchieLanguageConfiguration.setThreadLocalLogicalPathLanguage(null);
        ArchieLanguageConfiguration.setThreadLocalDescriptiongAndMeaningLanguage(null);
    }

    @Test
    public void fixableMatches() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("fixable_matches.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = (Locatable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        ItemTree itemTree = (ItemTree) root.itemAtPath("/data[id2]/events[id3]/data[id4]");

        // Add a second cluster with the boolean set to true
        Cluster cluster2 = new Cluster("id81", new DvText("Cluster"), new ArrayList<>());
        DvBoolean dvBoolean = new DvBoolean();
        dvBoolean.setValue(true);
        cluster2.addItem(new Element("id82", new DvText("First element"), dvBoolean));
        itemTree.addItem(cluster2);

        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("There are eleven values that must be set", 11, evaluate.getSetPathValues().size());

        //assert that paths must be set to specific values
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        assertEquals("at1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/terminology_id/value").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/value").getValue());
        assertEquals("at6", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value").getValue());
        assertEquals(0l, evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/value").getValue());
        assertEquals("at1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/defining_code/terminology_id/value").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/value").getValue());
        assertEquals("The boolean is true", evaluate.getSetPathValues().get("/data[id2]/events[id3, 1]/data[id4]/items[id81, 6]/items[id84]/value/value").getValue());

        //now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        assertEquals("at1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/terminology_id/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));
        assertEquals("at6", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value"));
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        assertEquals("at1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/defining_code/terminology_id/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/value"));
        assertEquals("The boolean is true", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3, 1]/data[id4]/items[id81, 6]/items[id84]/value/value"));

        //and of course the DV_ORDINAL and DV_CODED_TEXT should be constructed correctly, with the correct numeric respectively a textual value
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id8]/null_flavour/value"));


        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }

    }

    @Test
    public void andExpression() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("and.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = (Locatable) testUtil.constructEmptyRMObject(archetype.getDefinition());
        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("There are seven values that must be set", 7, evaluate.getSetPathValues().size());

        //assert that paths must be set to specific values
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        assertEquals("at1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string").getValue());
        assertEquals("Option 1", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/value").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/terminology_id/value").getValue());
        assertEquals("at6", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string").getValue());
        assertEquals("local", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value").getValue());
        assertEquals(0l, evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id7]/value/value").getValue());


        //now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        assertEquals("at1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/defining_code/code_string"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));
        assertEquals("at6", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/code_string"));
        assertEquals("local", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/symbol/defining_code/terminology_id/value"));
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));


        //and of course the DV_ORDINAL and DV_CODED_TEXT should be constructed correctly, with the correct numeric respectively a textual value
        assertEquals(0l, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id7]/value/value"));
        assertEquals("Option 1", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value/value"));


        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }

    }

    @Test
    public void constructOnlyNecessaryStructure() throws Exception {
        archetype = parser.parse(ParsedRulesEvaluationTest.class.getResourceAsStream("construct_only_necessary_structure.adls"));
        RuleEvaluation<Locatable> ruleEvaluation = getRuleEvaluation();

        Locatable root = rmObjectCreator.create(archetype.getDefinition());
        EvaluationResult evaluate = ruleEvaluation.evaluate(root, archetype.getRules().getRules());
        assertEquals("there must be three values that must be set", 1, evaluate.getSetPathValues().size());

        //assert that paths must be set to specific values
        assertEquals("test string", evaluate.getSetPathValues().get("/data[id2]/events[id3]/data[id4]/items[id5]/value/value").getValue());
        assertEquals(null, evaluate.getSetPathValues().get("d/ata[id2]/events[id3]/data[id4]/items[id6]/value"));

        //now assert that the RM Object cloned by rule evaluation has been modified with the new values for further evaluation
        assertEquals("test string", ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id5]/value/value"));
        assertEquals(null, ruleEvaluation.getRMRoot().itemAtPath("/data[id2]/events[id3]/data[id4]/items[id6]/value"));

        evaluate = ruleEvaluation.evaluate(ruleEvaluation.getRMRoot(), archetype.getRules().getRules());
        for(AssertionResult result:evaluate.getAssertionResults()) {
            assertTrue(result.getResult());
        }

    }

    private RuleEvaluation<Locatable> getRuleEvaluation() {
        return new RuleEvaluation<>(ArchieRMInfoLookup.getInstance(), archetype);
    }

}
