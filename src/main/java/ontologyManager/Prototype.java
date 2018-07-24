/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ontologyManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredPropertyAssertionGenerator;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

    // Para cada Interação:
    //     Abrir a onotologia; @
    //     Realizar operação; (Query ou Assertion)
    //     Raciocinar sobre; (Caso Assertion na etapa passada)
    //     Salvar; @

public class Prototype {
    
    public OWLOntologyManager man;
    public OWLDataFactory owlDf;
    public OWLOntology loadedOntology;
    public IRI defaultIRI;
    public OWLReasoner reasoner;

    public Prototype() {
        this.man = OWLManager.createOWLOntologyManager();
        this.owlDf = this.man.getOWLDataFactory();
    }
    
    public void loadOntology(String filePath) throws OWLOntologyCreationException{
        File file = new File(filePath);
        this.loadedOntology = this.man.loadOntologyFromOntologyDocument(file);
        this.defaultIRI = this.man.getOntologyDocumentIRI(loadedOntology);
        this.reasoner = new ReasonerFactory().createReasoner(loadedOntology);
        
        System.out.println(this.loadedOntology);
    }
    
    public void createOntology() throws OWLOntologyCreationException {
    	IRI IOR = IRI.create("http://www.example.com/FelipeOntology");
    	this.loadedOntology = this.man.createOntology(IOR);
        this.defaultIRI = this.man.getOntologyDocumentIRI(loadedOntology);
        this.reasoner = new ReasonerFactory().createReasoner(loadedOntology);
        
        System.out.println(this.loadedOntology);
        
    }
    
    public void saveOntology(String filePath) throws FileNotFoundException, OWLOntologyStorageException{
        File fileout = new File(filePath);
        this.man.saveOntology(this.loadedOntology, new FunctionalSyntaxDocumentFormat(), new FileOutputStream(fileout));
        this.loadedOntology = null;
    }
    
    public void ontologyAssert(String prologCommands) throws Exception{
        /*
        Types of assertion made by a Agent:
        Individual Assertion;
        Individual Property Assertion;
        Individual Data Property Assertion;          
         
        Individual Assertion format: 
            exists(Marcador). --> this.loadedOntology.add(df.getOWLDeclarationAxiom(this.owlDf.getOWLNamedIndividual(defaultIRI + "Marcador")))
        
        Individual Class Assertion: isA(class, individual).
        
        Individual Data Property Assertion format; dataProperty(marcador, data). ou  dataProperty(marcador, nbrdata)
        
        Individual Property Assertion format; property(marcador1,marcador2).
        
        */

        ArrayList<String> identifiers = new ArrayList<>();
        
        Pattern pattern = Pattern.compile("[a-z_A-Z0-9\\.]+");
        Matcher matcher = pattern.matcher(prologCommands);
        
        //if(!matcher.find())
        //    throw new Exception("Comando Invalido.");
        
        
        //-----------------------------------
        // Add one by one the matches to the identifiers arrayList
        //-----------------------------------
        while(matcher.find()){
            identifiers.add(matcher.group());
        }
        
        
        // Debug Print
        for(String word: identifiers)
            System.out.println(word);
        
        
        //-----------------------------------
        // Tests to see if the firs match is the "exists" identifier. Ment to be 
        // a operation to add individuals.
        //-----------------------------------
        if(identifiers.get(0).equals("exists")){
            this.assertExists(identifiers.get(1));
            return;
        }

        //-----------------------------------
        // Tests to see if the first match is the "isA" identifier. Ment to be 
        // a operation to assert that idividual in indentifier[1] is of class 
        // identifier[2]
        //-----------------------------------
        if(identifiers.get(0).equals("isA")){
            this.assertIsA(identifiers.get(2), identifiers.get(1));
            return;
        }
        
        //-----------------------------------
        // Data properties will be added through a "data" in front of the 
        // data property's identifier.
        //-----------------------------------
        
        pattern = Pattern.compile("data");
        matcher = pattern.matcher(identifiers.get(0));
        
        // Tests to see if the first identifier is indeed a data property one.
        if(matcher.find()){
            String propertyName = identifiers.get(0);
            propertyName = propertyName.replaceFirst("data", "");
            
            //Creates a pattern to regognize if the identifier is actually number.
            //Which will be denoted by the "nbr" tag added in front of the value.
            pattern = Pattern.compile("nbr[0-9\\.]+");
            matcher = pattern.matcher(identifiers.get(2));
            
            //Tests for "nbr" tag.
            if(matcher.find()){
                String number =  identifiers.get(2);
                number = number.replaceFirst("nbr", "");
                
                //Creates a patter to test if the number is a real or interger.
                pattern = Pattern.compile("\\.");
                matcher = pattern.matcher(number);
                
                if(matcher.find()){
                    // If there is a match assert the last ~identifier~ as a double.
                    this.assertDataPropertyToIndividual(propertyName, identifiers.get(1),Double.valueOf(number));
                }else
                     // Else as a Interger.
                    this.assertDataPropertyToIndividual(propertyName, identifiers.get(1),Integer.valueOf(number));
                
            } else
                
                // In case the value assigned is not a number it will be saved a string.
                this.assertDataPropertyToIndividual(propertyName, identifiers.get(1),identifiers.get(2));

            return;
        }
            
        //Any non specif data properties will be created trough this final function call here.
        this.assertObjectPropertyToIndividual(identifiers.get(0),identifiers.get(1),identifiers.get(2));
        
    }
    
    protected void assertExists(String individualId){
        OWLNamedIndividual newIndividual = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individualId);
        OWLDeclarationAxiom da = this.owlDf.getOWLDeclarationAxiom(newIndividual);
        System.out.println(da);
        this.loadedOntology.add(da);
    }
    
    protected void assertIsA(String classId, String individualId){
            OWLClass objClass = this.owlDf.getOWLClass(this.defaultIRI + "#" + classId);
            OWLNamedIndividual newIndividual = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individualId);
            OWLClassAssertionAxiom ca = this.owlDf.getOWLClassAssertionAxiom(objClass, newIndividual);
            this.loadedOntology.add(ca);
    }
    
    protected void assertDataPropertyToIndividual(String propertyId, String individualId, String data){
            OWLDataProperty property = this.owlDf.getOWLDataProperty(this.defaultIRI + "#" + propertyId);
            OWLNamedIndividual individual = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individualId);
            OWLDataPropertyAssertionAxiom dpa = this.owlDf.getOWLDataPropertyAssertionAxiom(property, individual, data);
            this.loadedOntology.add(dpa);
    }
    
    protected void assertDataPropertyToIndividual(String propertyId, String individualId, int data){
            OWLDataProperty property = this.owlDf.getOWLDataProperty(this.defaultIRI + "#" + propertyId);
            OWLNamedIndividual individual = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individualId);
            OWLDataPropertyAssertionAxiom dpa = this.owlDf.getOWLDataPropertyAssertionAxiom(property, individual, data);
            this.loadedOntology.add(dpa);
    }
    
    protected void assertDataPropertyToIndividual(String propertyId, String individualId, double data){
            OWLDataProperty property = this.owlDf.getOWLDataProperty(this.defaultIRI + "#" + propertyId);
            OWLNamedIndividual individual = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individualId);
            OWLDataPropertyAssertionAxiom dpa = this.owlDf.getOWLDataPropertyAssertionAxiom(property, individual, data);
            this.loadedOntology.add(dpa);
    }
    
    protected void assertObjectPropertyToIndividual(String propertyId, String individual1Id, String individual2Id){
        OWLObjectProperty  property = this.owlDf.getOWLObjectProperty (this.defaultIRI + "#" + propertyId);
        OWLNamedIndividual subject  = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individual1Id);
        OWLNamedIndividual object   = this.owlDf.getOWLNamedIndividual(this.defaultIRI + "#" + individual2Id);
        OWLObjectPropertyAssertionAxiom dpa = this.owlDf.getOWLObjectPropertyAssertionAxiom(property, subject, object);
        this.loadedOntology.add(dpa);
    }

    public void reason(){
        this.reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
        
        OWLOntologyMerger merger = new OWLOntologyMerger(this.man);
        
        
        List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
        //gens.add(new InferredSubClassAxiomGenerator());  
        //gens.add(new InferredClassAssertionAxiomGenerator()); // Infers subclasses 
        //gens.add(new InferredDisjointClassesAxiomGenerator());
        //gens.add(new InferredEquivalentClassAxiomGenerator());
        //gens.add(new InferredEquivalentDataPropertiesAxiomGenerator());
        //gens.add(new InferredEquivalentObjectPropertyAxiomGenerator());
        //gens.add(new InferredInverseObjectPropertiesAxiomGenerator());
        //gens.add(new InferredObjectPropertyCharacteristicAxiomGenerator());
        gens.add(new InferredPropertyAssertionGenerator());
        //gens.add(new InferredSubDataPropertyAxiomGenerator());
        //gens.add(new InferredSubObjectPropertyAxiomGenerator());

         InferredOntologyGenerator iog = new InferredOntologyGenerator(this.reasoner, gens);
         iog.fillOntology(this.owlDf, this.loadedOntology);
    }
    
    public String ontologyQuery(String classExpressionString){
        
        DLQueryParser parser = new DLQueryParser(reasoner.getRootOntology(), new SimpleShortFormProvider());
        
        OWLClassExpression classExp = parser.parseClassExpression(classExpressionString);
        
        NodeSet<OWLNamedIndividual> nodeIndividuals = reasoner.getInstances(classExp, true);
        
        Set<OWLNamedIndividual> individuals = nodeIndividuals.getFlattened();
        
        String answer = "";
        
        for(OWLNamedIndividual i: individuals){
            answer += i + "\n";
        }
        
        return answer;
        
        /*
        System.out.println(this.owlDf);
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(this.owlDf, classExpressionString);
        parser.setDefaultOntology(this.loadedOntology);
        
        
        
        ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
        
        BidirectionalShortFormProviderAdapter bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(this.man, this.loadedOntology.getImportsClosure(), shortFormProvider);
        
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        

        OWLClassExpression classExp = parser.parseClassExpression();
        
        NodeSet<OWLNamedIndividual> nodeIndividuals = reasoner.getInstances(classExp, true);
        Set<OWLNamedIndividual> individuals = nodeIndividuals.getFlattened();
        
        String answer = "";
        
        for(OWLNamedIndividual i: individuals){
            answer += i + "\n";
        }
        

        return answer;

        */
    }
    
    class DLQueryParser {
    private final OWLOntology rootOntology;
    private final BidirectionalShortFormProvider bidiShortFormProvider;

    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);
    }

    public OWLClassExpression parseClassExpression(String classExpressionString) {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        System.out.println(parser);

        parser.setDefaultOntology(rootOntology);
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        return parser.parseClassExpression();
        }
    }
    
    
    
    
    
    
    
    
    
    
}
