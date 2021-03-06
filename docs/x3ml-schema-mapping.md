#X3ML Schema Mapping
---
## Introduction

The X3ML language was designed on the basis of work that was done by FORTH around 2006. It was adapted primarily to be more according to the DRY principle (avoiding repetition) and to be more explicit in its contract with the URI Generating process.

The requirement that X3ML mappings be built **collaboratively** and that they must be amenable to sharing via an accumulating **Mapping Memory** also has influenced its design. The languge must be explicit about any knowledge of underlying ontologies so that the engine need not access that knowledge in order to function, and so that the mapping memory can also independently exchange X3ML.

For the time being, X3ML will be restricted to consuming XML records and producing RDF in various serializations.  As a result, XPath will be the source access used.

## Mappings

At first glance, the global structure of X3ML is quite easy to understand.  It consists of some prerequisite information, and then a series of mappings.

	<x3ml version="0.1" sourceType="XPATH">
		<info/>
	    <namespaces/>
	    <mappings>
		    <mapping>
		        <domain>
		            <source_node/>
		            <target_node/>
		        </domain>
		        <link>
		            <path>
		                <source_relation/>
		                <target_relation/>
		            </path>
		            <range>
	            	    <source_node/>
	        	        <target_node/>
	    	        </range>
		        </link>
		        <link/>
	        	...
	    	</mapping>
		    <mapping/>
		    ...
	    <mappings>
	    <comments/>
	</x3ml>

Each *mapping* contains a *domain* and a number of *links*, and links are built of of a *path* and a *range*.

## Conditions

Whether or not the engine will continue to pursue the creation of a particular target element can depend on whether certain conditions are met. This is intended to allow mappings to be "activated" like rules, which are often based on terminology usage in the source.

The elements used will be *exists*, *equals*, *narrower*, *and*, *or*, and *not*, each enclosed in an **if** element.

    <if>...</if>

Conditions can be situated in the three different target blocks within the mapping definition:

	<x3ml version="0.1" sourceType="XPATH">
	    <namespaces/>
	    <mappings>
		    <mapping>
		        <domain>
		            <source_node/>
		            <target_node>
		            	<if/>
		            </target_node>
		        </domain>
		        <link>
		            <path>
		                <source_relation/>
		                <target_relation>
							<if/>
		                </target_relation>
		            </path>
		            <range>
		                <source_node/>
		                <target_node>
							<if/>
		                </target_node>
		            </range>
		        </link>
		        <link/>
		        ...
		    </mapping>
		    <mapping/>
		    ...
	    <mappings>
	</x3ml>

Existence can be checked, as well as equality:

    <if><exists>...</exists></if>
    <if><not><if><exists>...</exists></if></not></if>
    <if><equals value="...">...</equals></if>
    <if><not><if><equals value="...">...</equals></if></not></if>

An option that will be implemented later when there is a SKOS vocabulary available for querying:

    <if><narrower value="...">...</narrower></if>

Multiple conditions can also be combined into boolean expressions:

    <if>
		<and>
			<if/>
			<if/>
			...
		</and>
    </if>
    <if>
		<or>
			<if/>
			<if/>
			...
		</or>
    </if>

## Source

The source element provides the engine with the information needed to navigate the source record. The expected content depends on the top-level attribute *source_type*, which will only allow "XPATH" for the time being.

First, the source of the *domain* is used as a kind of "anchor" and then the *links* are traversed such that their rules determine what RDF statements can be made about the source.

	<source_node>...</source_node>
	<source_relation>...</source_relation>

The *source* element is also present in path and range, and these sources are evaluated within the context of the domain/source.  The two are typically identical, but they represent a statement about the semantic origin of the resulting relationship and entity.  When they are not identical, the range/source extends the path/source

## Target

The domain and range contain *target* blocks, which can either contain/generate URIs or represent literals:

	<target>
	    <relationship>...</relationship>
	</target>

	<target>
		<entity>
			<class>...</class>
			<value_generator name="...">
			    <arg name="...">...</arg>
			    <arg/>
			    ...
			</value_generator>
		</entity>
	</target>

	<target>
		<entity>
			<class>...</class>
			<constant>...</constant>
		</entity>
	</target>

The arguments of the **[Value Generator](x3ml-value-generation.md)** are named and optional, since there is logic within the URI Generation to anticipate omissions.  The type of argument is determined by the value function, since the argument values are requested according to type.  The *xpath* expressions which will be evaluated in the current context (see Source below) in order to fetch information from the source record.

## Intermediate Nodes

Sometimes a path in the source schema needs to become more than just a path in the output.  Instead, an intermediate entity must be introduced.

	source:
	record => has_descriptor => term
	
	target:
	man-made-object => was_produced_by => PRODUCTION => used_general_technique => type

This is formulated using the *intermediate* element:

	<path>
		<source_relation/>
		<target_relation>
			<relationshihp/>
			<entity/>
			<relationship/>
		</target>
	</path>

## Additional Nodes

When additional properties and entities need to be added to a target entity, the *additional* element can be used.  It contains the entity which will be attached to the target entity, and the relationship describing the link.

	<range>
		<source_node/>
		<target_node>
			<entity>
				...
				<additional>
					<relationship/>
					<entity/>
				</additional>
				<additional/>
				...
			</entity>
		</target_node>
	</range>

Note that the target allows multiple additional nodes.

## Info and Comments

Tools for managing X3ML will make use of these note elements to record all information arising from the mapping building process which may be useful people accessing the *Mapping Memory* in the future.  

A simple open-ended structure where each *comment* has a **type** and content allows for different tools to easily define their own note structures.  We could maintain a definition of what the different known **type** values are, and this could be easily expanded without changing the schema.

	<info>
		... various fields describing the mapping ...
	</info>
	<comments>
		<comment type="...">
			....
		</comment>
		<comment/>
		...
	</comments>

There are *comments* elements defined at the top level where all mappings are bound together, as well as in each *domain* and each *path* and *range*.

The content of a note can be any arbitrary XML, and the X3ML Engine will ignore these blocks, since they are for human consumption.


---

Contact: Gerald de Jong &lt;gerald@delving.eu&gt;



















--

