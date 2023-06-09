# import re
#
# owl_code = '''
# <?xml version="1.0"?>
#
# <!DOCTYPE rdf:RDF [
#     <!ENTITY dcterms "http://purl.org/dc/terms/" >
#     <!ENTITY vann "http://purl.org/vocab/vann/" >
#     <!ENTITY owl "http://www.w3.org/2002/07/owl#" >
#     <!ENTITY qu "http://purl.org/NET/ssnx/qu/qu#" >
#     <!ENTITY dc "http://purl.org/dc/elements/1.1/" >
#     <!ENTITY xsd "http://www.w3.org/2001/XMLSchema#" >
#     <!ENTITY mthreelite "http://purl.org/iot/vocab/m3-lite#" >
#     <!ENTITY ssn "http://purl.oclc.org/NET/ssnx/ssn#" >
#     <!ENTITY xml "http://www.w3.org/XML/1998/namespace" >
#     <!ENTITY rdfs "http://www.w3.org/2000/01/rdf-schema#" >
#     <!ENTITY geo "http://www.w3.org/2003/01/geo/wgs84_pos#" >
#     <!ENTITY rdf "http://www.w3.org/1999/02/22-rdf-syntax-ns#" >
#     <!ENTITY qudt "http://data.qudt.org/qudt/owl/1.0.0/unit.owl#" >
#     <!ENTITY vs "http://www.w3.org/2003/06/sw-vocab-status/ns#" >
#     <!ENTITY iot-lite "http://purl.oclc.org/NET/UNIS/fiware/iot-lite#" >
# ]>
#
#
# <rdf:RDF xmlns="http://purl.org/iot/vocab/m3-lite#"
#      xml:base="http://purl.org/iot/vocab/m3-lite"
#      xmlns:ssn="http://purl.oclc.org/NET/ssnx/ssn#"
#      xmlns:vann="http://purl.org/vocab/vann/"
#      xmlns:vs="http://www.w3.org/2003/06/sw-vocab-status/ns#"
#      xmlns:qudt="http://data.qudt.org/qudt/owl/1.0.0/unit.owl#"
#      xmlns:dcterms="http://purl.org/dc/terms/"
#      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
#      xmlns:mtheelite="http://purl.org/iot/vocab/m3-lite#"
#      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
#      xmlns:qu="http://purl.org/NET/ssnx/qu/qu#"
#      xmlns:dc="http://purl.org/dc/elements/1.1/"
#      xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#"
#      xmlns:iot-lite="http://purl.oclc.org/NET/UNIS/fiware/iot-lite#"
#      xmlns:xml="http://www.w3.org/XML/1998/namespace"
#      xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
#      xmlns:owl="http://www.w3.org/2002/07/owl#">
#
#     <owl:Ontology rdf:about="http://purl.org/iot/vocab/m3-lite#">
#         <owl:versionInfo rdf:datatype="&xsd;decimal">3.1</owl:versionInfo>
#         <dcterms:issued rdf:datatype="&xsd;date">2015-07-02</dcterms:issued>
#         <rdfs:comment xml:lang="en">The M3-lite is a taxonomy that enables testbeds to semantically annotate the IoT data produced by heterogeneous devices and store them in a federated datastore such as FIESTA-IoT. In this taxonomy, we classify devices, the domain of interests (health, smart home, smart kitchen, environmental monitoring, etc.), phenomena and unit of measurements.</rdfs:comment>
#         <vann:preferredNamespacePrefix>m3-lite</vann:preferredNamespacePrefix>
#         <vann:preferredNamespaceUri>http://purl.org/iot/vocab/m3-lite#</vann:preferredNamespaceUri>
#         <dc:title xml:lang="en">The M3-lite Taxonomy</dc:title>
#         <dcterms:modified rdf:datatype="&xsd;date">2017-11-27</dcterms:modified>
#         <dc:contributor xml:lang="en">Rachit Agarwal, Inria, Paris</dc:contributor>
#         <dc:contributor xml:lang="en">David Gomez, UC, Santander</dc:contributor>
#         <dc:contributor xml:lang="en">Tarek Elsaleh, UNIS, Surrey</dc:contributor>
#         <dc:contributor xml:lang="en">Luis Sanchez, UC, Santander</dc:contributor>
#         <dc:contributor xml:lang="en">Jorge Lanza, UC, Santander</dc:contributor>
#         <dc:contributor xml:lang="en">Amelie Gyrard, NUIG, Galway</dc:contributor>
#         <dc:contributor xml:lang="en">Garvita Bajaj, Inria, Paris (Past)</dc:contributor>
#         <dc:description xml:lang="en">M3-lite taxonomy is designed for the EU H2020 FIESTA-IoT project. It is a lite version of M3 ontology. M3-lite contains taxonomy for various QuantityKinds (commonly known as physical and environmental phenomena), Unit of measurements, different types of sensor and different types of domain of interests. M3-lite has evolved to serve the purpose of several testbeds that want to be part of federation provided by FIESTA-IoT. Most relevant testbeds integrated using M3-lite are EU FP7's SmartSantander testbed, UniS's (UK) SmartICS testbed, Inria's (France) Soundcity/Ambiciti testbed, KETI's (South Korea) Mobius testbed and EGM's (France) 4G testbed. More information on the above testbeds  along with those that have joined at the FIESTA-IoT platform is available via http://fiesta-iot.eu/index.php/fiesta-testbeds/. Intially, this taxonomy was a light weight version of M3 ontology and since 2016 has found its own separate way.
#
# We further acknowledge, testbed provider such as SmartSantander, UniS, KETI, Inria, and other open call testbeds for their valuable comments. Please report any errors to rachit.agarwal@inria.fr.
#
#         </dc:description>
#         <dc:rights>Copyright EU H2020 FIESTA-IoT</dc:rights>
#     </owl:Ontology>
#
#
#
#     <!--
#     Last updated: November 27 2017
#     Rachit Agarwal (INRIA)
#         - added RSSI, SNR, LORAinteface, HDOP, count QK subclasses.
#
#
#     Last updated: July 29 2017
#     Rachit Agarwal (INRIA)
#         - Added iot-lite:ActuatingDevice subclass
#             VoiceCommandController
#
#         - Added ssn:SensingDevice subclasses
#             VOCSensor, ElectricalSensor, IEEE802154InterfaceEnergyMeter, BoardVoltageSensor, DeviceUptimeClock, VoiceCommandSensor, DirectionOfArrivalSensor
#
#         - added qu:QuantityKind subclasses
#             ChemicalAgentAtmosphericConcentrationNO, ChemicalAgentAtmosphericConcentrationSO2, ChemicalAgentAtmosphericConcentrationVOC, DeviceUptime, VoiceCommand, DirectionOfArrival
#
#         - added qu:Unit subclasses
#             PartsPerBillion, DecibelMilliwatt
#
#
#     Last updated: April 20 2017
#     Rachit Agarwal (INRIA)
#         - Converting all to British English (Meter to Metre and Liter to litre) (changes in UNITS, QKS, etc.)
#         - QuantityKinds Changes
#             - chemicalAgentAtmosphericConcentrationO3, intDlThroughputKbps,intUlPacketLoss,intUlThroughputKbps Now start with uppercase
#             - ElectricCurrent prefix change to m3-lite from QUDT
#             - ChemicalAgentAtmosphericConcentration now a subclass of ChemicalAgentConcentration
#
#         - QuantityKinds Added
#             - Count (Number of available particular things.)
#             - CountAvailableBicycles (Number of available bicycles at a particular bicycle docking station.)
#             - CountAvailableTaxis (Number of taxis available at a particular taxi stop.)
#             - CountEmptyDockingPoints (Number of empty docking points at a particular bicycle docking station.)
#             - TrafficIntensity (The intensity of a traffic flow is the number of vehicles passing a cross section of a road in a unit of time.)
#             - ChemicalAgentWaterConcentration as subclass of ChemicalAgentConcentration
#             - ChemicalAgentWaterConcentrationO2, ChemicalAgentWaterConcentrationNH4Ion, ChemicalAgentWaterConcentrationNO3Ion as subclasses of ChemicalAgentWaterConcentration
#             - ChemicalAgentAtmosphericConcentrationDust
#             - IonisingRadiation
#             - Voltage
#
#         - SensingDevices Changes
#             - VehicleCountSensor is now a subclass of Counter.
#             - SoilHumiditySensor made subclass of HumiditySensor
#             - For PHSensor added domainOfInterest Environment.
#             - VehicleCountSensor subclass of Counter
#             - Thermometer now has subclasses (SoilThermometer, WaterThermometer, BoardThermometer BodyThermometer, RoadSurfaceThermometer, AirThermometer)
#             - changed OccupancyDetector to PresenceDetector
#
#         - SensingDevices Added
#             - ElectricFieldSensor (Scientific instrument for measuring electromagnetic fields (EMF). Most of them measure the electromagnetic radiation flux density (DC fields) or the change in an electromagnetic field over time (AC fields).)
#             - Counter (Sensors that reckons occurrences or repetitions of physical objects, phenomena or events.)
#             - DoorStateSensor (This sensor detects if a door is in the state of OPEN or CLOSED.)
#             - SoilThermometer (This sensor reports Soil temperature.)
#             - WaterThermometer (This sensor reports Water temperature.)
#             - WaterPHSensor (Device used to detect PH level of water. Sub Class of PhSensor)
#             - WaterConductivitySensor (Device used to measure the conductivity of water. subclass of ConductivitySensor)
#             - Air Humidity Sensor added and made subclass of Humidity sensor
#             - Added WaterO2IonSensor, WaterNH4IonSensor, WaterNO3IonSensor, LoRaInterfaceEnergyMeter, WiFiInterfaceEnergyMeter, clock
#         hemicalAgentAtmosphericConcentration
#             - Dust sensor, Optical Dust sensor
#             - RadiationParticleDetector
#
#         - Units Changes
#             - Bar and DegreeAngle prefix change to m3-lite from QUDT
#             - MicroWatt changed to  Microwatt
#             - WattPerSquareMeter had incorrect subclassing reference
#
#         - Units Added
#             - Item (Each of the accountable elements within a group)
#             - SiemensPerMetre (Conductivity is measured in Siemens per meter (S/m).)
#             - MilligramPerLitre (Level of Dissolved substance in liquid measured in mg per litre.)
#             - RadiationParticlesPerMinute
#
#     Last updated: Febuary 09 2017
#     Rachit Agarwal (INRIA)
#         - Major release
#         - fix comments and add wherever missing
#         - clean descriptions for the Abstract and Introduction part
#         - clean properties
#         - remove direction as subclass of QK based on description it is same as measurement Type.
#         - EnergyFOI to EnergyDOI
#         - Refactor, remove unused (Subclasses of subclasses of DOI, RFID QKs, Kilo (replicated with kilogram), Altitude, Latitude, Longitude, PositionLatitude, PositionLongitude, Position, Time, NumberSteps)
#         - Changed PresenceState to Presence.
#         - fix duplicates
#         - Subclassing:
#             - Secondtime (base class)
#             The suffix "Time" comes in those ones that clash with "Angle"
#                 + Day
#                 + Hour
#                 + MinuteTime
#                 + MillisecondTime
#                 + MicrosecondTime...
#             - Meter (base class)
#                 + Kilometer
#                 + Millimeter
#                 + Inch
#                 + Mile...
#             - Gram (base class)
#                 + Kilogram
#                 + Milligram
#                 + Pound
#
#     Last updated: November 23 2016
#     Rachit Agarwal (INRIA)
#         - change object property domainOFInterest to hasDomainOfInterest
#         - remove txn
#
#     Last updated: October 28 2016
#     Rachit Agarwal (INRIA)
#         - Add Com4Innov Classes
#         - Fix typos
#
#     Last updated: September 25 2016
#     Rachit Agarwal (INRIA)
#     David Gomez (UniCan)
#         - Follow naming convention
#         - Add Labels whereever missing
#         - remove VAR, it was replicating Volt Ampere Reactive
#
#     Last updated: Aug 16 2016
#     Rachit Agarwal (INRIA)
#         - remove disjoint axioms as it was logical error
#
#     Last updated: August 5,2016
#     Garvita Bajaj (INRIA)
#         - introduce a new concept m3-lite:Source and make m3-lite:SoundSource a subclass of it.
#         - Defined a new class: m3-lite:Others, which acts as subclass for m3-lite:SoundSource, m3-lite:Unit,m3-lite:QuantityKind, m3-lite:MeasurementType, m3-lite:Source
#         - added property m3-lite:hasDirection with range as m3-lite:Direction. Since the domain of this property will be ssn:Observation, we leave the domain undefined here
#         - modify the m3-lite:hasSource property to have domain as m3-lite:QuantityKind and range as the new concept m3-lite:Source
#
#     Last updated: July 19, 2016
#     Rachit Agarwal (INRIA)
#         - Added new Quantity Kind (Proximity, RecognizedActivity)
#         - Added new Sensor (Proximity)
#         - Added MeasurementType and SoundSource concept
#         - Added object Properties hasMeasurementType, hasSource, and hasSoundSource
#
#     Last updated: July 7, 2016
#     Garvita Bajaj, Rachit Agarwal (INRIA)
#         - Touch sensor and image sensor added as subclasses of 'm3-lite:SensingDevice' class.
#         - A further classification of touch sensors can be provided based on whether they are capacitive or resistive,
#
#
#     Last updated: April 25, 2016
#     Rachit Agarwal (INRIA)
#         - Added comments
#
#     Last updated: March 07, 2016
#     Rachit Agarwal (INRIA)
#         - Merge Amelie and David versions
#         - m3-lite:TaggingDevice has been changed by iot-lite:TagDevice
#         - check KETI Quantity Kind and units
#             * Difference between active power and reactive power?
#         - Add new quantity kind if not already available from Com4Innov:
#             * Quantity Kind Unit
#             * AirTemperature    Celsius
#             * Board Temperature (temp inside the device Celsius -> added)
#             * Dewpoint* Celsius -> added
#             * Delta Dewpoint**  Natural number (W/out unit) -> added
#             * Humidity  Percentage
#             * Sound Level   dB
#             * Radiation W/m^2
#             * Output voltage (Vout) Volt -> added but maybe redundancy with active power?
#
#    Some minor issues:
#    Amelie Gyrard (NUIG)
#         - Duplication: MeterPerSecondSquare and MeterPerSecondSquared?
#         - chemicalAgentAtmosphericConcentrationO3 c upper case?
#         - duplication rainfall - precipitation, which one shoud we remove
#         - duplication humidity - relative humidity, which one shoud we remove?
#         - duplication RFID and RFIDTag
#
#     February 24, 2016
#     Amelie Gyrard (NUIG)
#         - ssn:Sensor replaced by ssn:SensingDevice
#         - I added the tagging device hierarchy (RFID, QRCode, NFC, Barcode) as a subclass of ssn:Device
#         - update all FIESTA-IoT tools using the M3-lite taxonomy (resource registry)
#
#         - TO DO:
#         ssn:FeatureOfInterest -> iot-lite:DomainOfInterest (should be updated in iot-lite)
#
#
#     October 20, 2015
#     Amelie Gyrard (NUIG)
#         - v.0.1 M3-lite published online following the PURL namespace
#
#     October 27, 2015
#     Amelie Gyrard (NUIG)
#         - New class for citypulse testbed VehicleCountSensor
#         - Add rdfs:comments for all subclasses of ssn:featureOfInterest
#         - Clean labels
#         - TO DO: Add comments for each concept/property
#
#     -->
#
#     <!--
#     ///////////////////////////////////////////////////////////////////////////////////////
#     //
#     // Annotation properties
#     //
#     ///////////////////////////////////////////////////////////////////////////////////////
#      -->
#
#
#
#     <!-- http://purl.org/dc/elements/1.1/description -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/description"/>
#
#
#
#     <!-- http://purl.org/dc/elements/1.1/title -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/title"/>
#
#
#
#     <!-- http://purl.org/dc/terms/issued -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/terms/issued"/>
#
#
#
#     <!-- http://purl.org/dc/terms/modified -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/terms/modified"/>
#
#
#
#     <!-- http://purl.org/vocab/vann/preferredNamespacePrefix -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/vocab/vann/preferredNamespacePrefix"/>
#
#
#
#     <!-- http://purl.org/vocab/vann/preferredNamespaceUri -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/vocab/vann/preferredNamespaceUri"/>
#
#
#
#     <!-- http://www.w3.org/2000/01/rdf-schema#comment -->
#
#     <owl:AnnotationProperty rdf:about="&rdfs;comment"/>
#
#
#
#     <!-- http://www.w3.org/2002/07/owl#equivalentClass -->
#
#     <owl:AnnotationProperty rdf:about="http://www.w3.org/2002/07/owl#equivalentClass"/>
#
#
#     <!-- http://purl.org/dc/elements/1.1/creator -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/creator"/>
#
#
#     <!-- http://purl.org/dc/elements/1.1/contributor -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/contributor"/>
#
#
#
#     <!-- http://purl.org/dc/elements/1.1/rights -->
#
#     <owl:AnnotationProperty rdf:about="http://purl.org/dc/elements/1.1/rights"/>
#
#     <!-- http://www.w3.org/2002/07/owl#versionInfo -->
#
#     <owl:AnnotationProperty rdf:about="http://www.w3.org/2002/07/owl#versionInfo"/>
#
#
#         <!-- http://www.w3.org/2000/01/rdf-schema#isDefinedBy -->
#
#     <owl:AnnotationProperty rdf:about="&rdfs;isDefinedBy"/>
#
#
#
#     <!-- http://www.w3.org/2000/01/rdf-schema#label -->
#
#     <owl:AnnotationProperty rdf:about="&rdfs;label"/>
#
#
#
#     <!-- http://www.w3.org/2000/01/rdf-schema#seeAlso -->
#
#     <owl:AnnotationProperty rdf:about="&rdfs;seeAlso"/>
#
#     <!--
#     ///////////////////////////////////////////////////////////////////////////////////////
#     //
#     // Object Properties
#     //
#     ///////////////////////////////////////////////////////////////////////////////////////
#      -->
#
#
#
#
#     <!-- http://purl.org/iot/vocab/m3-lite#hasDomainOfInterest -->
#
#     <owl:ObjectProperty rdf:about="&mthreelite;hasDomainOfInterest">
#         <rdfs:range rdf:resource="&mthreelite;DomainOfInterest"/>
#         <rdfs:comment xml:lang="en">This property is used to classify devices by DomainOfInterest (e.g., blood pressure sensor is used in healthcare).</rdfs:comment>
#         <rdfs:label xml:lang="en">has Domain Of Interest</rdfs:label>
#     </owl:ObjectProperty>
#
#
#
#     <!-- http://purl.org/iot/vocab/m3-lite#hasDirection -->
#
#     <owl:ObjectProperty rdf:about="&mthreelite;hasDirection">
#         <rdfs:range>
#             <owl:Restriction>
#                 <owl:onProperty rdf:resource="&mthreelite;hasDirection"/>
#                 <owl:maxQualifiedCardinality rdf:datatype="&xsd;nonNegativeInteger">1</owl:maxQualifiedCardinality>
#                 <owl:onClass rdf:resource="&mthreelite;Direction"/>
#             </owl:Restriction>
#         </rdfs:range>
#         <rdfs:comment xml:lang="en">The observations made by the sensors are affected by the direction of the sensing device. This property allows observations of the sensor to be associated to the Direction concept.</rdfs:comment>
#         <rdfs:label xml:lang="en">has Direction</rdfs:label>
#     </owl:ObjectProperty>
#
#
#
#     <!-- http://purl.org/iot/vocab/m3-lite#hasMeasurementType -->
#
#     <owl:ObjectProperty rdf:about="&mthreelite;hasMeasurementType">
#         <rdfs:range>
#             <owl:Restriction>
#                 <owl:onProperty rdf:resource="&mthreelite;hasMeasurementType"/>
#                 <owl:maxQualifiedCardinality rdf:datatype="&xsd;nonNegativeInteger">1</owl:maxQualifiedCardinality>
#                 <owl:onClass rdf:resource="&mthreelite;MeasurementType"/>
#             </owl:Restriction>
#         </rdfs:range>
#         <rdfs:comment xml:lang="en">Each sensing device can have a different sensing mechanism which may result in different kinds of sensor data. This property links observation of the sensor to the associated MeasurementType.</rdfs:comment>
#         <rdfs:label xml:lang="en">has Measurement Type</rdfs:label>
#     </owl:ObjectProperty>
#
#
#
#     <!-- http://purl.org/iot/vocab/m3-lite#hasSoundSource -->
#
#     <owl:ObjectProperty rdf:about="&mthreelite;hasSoundSource">
#         <rdfs:subPropertyOf rdf:resource="&mthreelite;hasSource"/>
#         <rdfs:domain rdf:resource="&mthreelite;Sound"/>
#         <rdfs:range rdf:resource="&mthreelite;SoundSource"/>
#         <rdfs:comment xml:lang="en">This property links sound to its environmental source.</rdfs:comment>
#         <rdfs:label xml:lang="en">has Sound Source</rdfs:label>
#     </owl:ObjectProperty>
#
#
#
#     <!-- http://purl.org/iot/vocab/m3-lite#hasSource -->
#
#     <owl:ObjectProperty rdf:about="&mthreelite;hasSource">
#         <rdfs:domain rdf:resource="&qu;QuantityKind"/>
#         <rdfs:range rdf:resource="&mthreelite;Source"/>
#         <rdfs:comment xml:lang="en">This property links sensed phenomena to its environmental source.</rdfs:comment>
#         <rdfs:label xml:lang="en">has Source</rdfs:label>
#     </owl:ObjectProperty>
#     <owl:Class rdf:about="&mthreelite;PeopleStayDurationSensor">
#          <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
#          <rdfs:comment xml:lang="en">Device used to measure the time people stays within an area.</rdfs:comment>
#          <rdfs:label xml:lang="en">People Stay Duration Sensor</rdfs:label>
#      </owl:Class>
#
#      <!-- http://purl.org/iot/vocab/m3-lite#PeopleStayDurationAverage  -->
#
#      <owl:Class rdf:about="&mthreelite;PeopleStayDurationAverage">
#          <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
#          <rdfs:comment xml:lang="en">Average time people stays within an area.</rdfs:comment>
#          <rdfs:label xml:lang="en">People Stay Duration Average </rdfs:label>
#      </owl:Class>
#
#      <!-- http://purl.org/iot/vocab/m3-lite#PeopleFlowCountSensor -->
#
#      <owl:Class rdf:about="&mthreelite;PeopleFlowCountSensor">
#          <rdfs:subClassOf rdf:resource="&mthreelite;PeopleCountSensor"/>
#          <rdfs:comment xml:lang="en">Device used to count the number of people the moves from an area towards another area during a time window.</rdfs:comment>
#          <rdfs:label xml:lang="en">People Flow Count Sensor</rdfs:label>
#      </owl:Class>
#
#      <!-- http://purl.org/iot/vocab/m3-lite#CountPeopleMoving -->
#
#      <owl:Class rdf:about="&mthreelite;CountPeopleMoving">
#          <rdfs:subClassOf rdf:resource="&mthreelite;CountPeople"/>
#          <rdfs:comment xml:lang="en">Number of people that moved outside an area towards another area during a time window.</rdfs:comment>
#          <rdfs:label xml:lang="en">Count People Moving</rdfs:label>
#      </owl:Class>
#
#
#
#
# </rdf:RDF>
#
#
# <!-- Generated by the OWL API (version 4.2.1.20160306-0033) https://github.com/owlcs/owlapi -->
#
# '''
#
# pattern = r'(<owl:Class rdf:about="[^"]+">.*?</owl:Class>)'
# matches = re.findall(pattern, owl_code, re.DOTALL)
# for match in matches:
#     print(match.strip())

import re

text = '''
<owl:Class rdf:about="&mthreelite;Window">
        <rdfs:subClassOf rdf:resource="&iot-lite;ActuatingDevice"/>
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="&mthreelite;hasDomainOfInterest"/>
                <owl:someValuesFrom rdf:resource="&mthreelite;BuildingAutomation"/>
            </owl:Restriction>
        </rdfs:subClassOf>
        <rdfs:comment xml:lang="en">An actuator to automatically open/close the window.</rdfs:comment>
        <rdfs:label xml:lang="en">Window Actuating Device</rdfs:label>
    </owl:Class>


    <!-- NOT SURE -->
    <!-- http://purl.org/iot/vocab/m3-lite#WorkingState -->

    <owl:Class rdf:about="&mthreelite;WorkingState">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:label xml:lang="en">Working State</rdfs:label>
        <rdfs:comment xml:lang="en">Indicator of whether a person (or object) is working or not</rdfs:comment>
    </owl:Class>


    <!-- NOT SURE -->
    <!-- http://purl.org/iot/vocab/m3-lite#WorkingStateDriver -->

    <owl:Class rdf:about="&mthreelite;WorkingStateDriver">
        <rdfs:subClassOf rdf:resource="&mthreelite;WorkingState"/>
        <rdfs:label xml:lang="en">Working State Driver</rdfs:label>
        <rdfs:comment xml:lang="en">Indicator of whether the driver of a vehicle is present or not</rdfs:comment>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#WorkingStateDriver1 -->

    <owl:Class rdf:about="&mthreelite;WorkingStateDriver1">
        <rdfs:subClassOf rdf:resource="&mthreelite;WorkingStateDriver"/>
        <rdfs:comment xml:lang="en">State of work of the first driver as defined in the FMS standard.</rdfs:comment>
        <rdfs:label xml:lang="en">Working State Driver 1</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#WorkingStateDriver2 -->

    <owl:Class rdf:about="&mthreelite;WorkingStateDriver2">
        <rdfs:subClassOf rdf:resource="&mthreelite;WorkingStateDriver"/>
        <rdfs:comment xml:lang="en">State of work of the second driver as defined in the FMS standard.</rdfs:comment>
        <rdfs:label xml:lang="en">Working State Driver 2</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#Wout -->

    <owl:Class rdf:about="&mthreelite;Wout">
        <rdfs:subClassOf rdf:resource="&qu;Unit"/>
        <rdfs:comment xml:lang="en">This unit is used to measure delta dew point within the Com4Innov tesbed. Natural number (W/out unit).</rdfs:comment>
        <rdfs:label xml:lang="en">W/out</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#Year -->

    <owl:Class rdf:about="&mthreelite;Year">
        <rdfs:subClassOf rdf:resource="&mthreelite;SecondTime"/>
        <rdfs:comment xml:lang="en">Year as a unit of time.</rdfs:comment>
        <rdfs:label xml:lang="en">Year</rdfs:label>
        <rdfs:seeAlso rdf:resource="http://sweet.jpl.nasa.gov/ontology/units.owl#year"/>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationO3 -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationO3">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">The concentration of ozone (O3) gas suspended in the atmosphere.</rdfs:comment>
        <rdfs:label xml:lang="en">Chemical Agent Atmospheric Concentration O3</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#IntDlThroughputKbps -->

    <owl:Class rdf:about="&mthreelite;IntDlThroughputKbps">
        <rdfs:subClassOf rdf:resource="&mthreelite;Communication"/>
        <rdfs:comment xml:lang="en">Downlink Throughput.</rdfs:comment>
        <rdfs:label xml:lang="en">Downlink Throughput</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#IntUlPacketLoss -->

    <owl:Class rdf:about="&mthreelite;IntUlPacketLoss">
        <rdfs:subClassOf rdf:resource="&mthreelite;Communication"/>
        <rdfs:comment xml:lang="en">Uplink Packet Loss.</rdfs:comment>
        <rdfs:label xml:lang="en">Uplink Packet Loss</rdfs:label>
    </owl:Class>



    <!-- http://purl.org/iot/vocab/m3-lite#IntUlThroughputKbps -->

    <owl:Class rdf:about="&mthreelite;IntUlThroughputKbps">
        <rdfs:subClassOf rdf:resource="&mthreelite;Communication"/>
        <rdfs:comment xml:lang="en">Uplink Throughput.</rdfs:comment>
        <rdfs:label xml:lang="en">Uplink Throughput</rdfs:label>
    </owl:Class>

<!-- http://purl.org/iot/vocab/m3-lite#Item -->

    <owl:Class rdf:about="&mthreelite;Item">
        <rdfs:subClassOf rdf:resource="&qu;Unit"/>
        <rdfs:comment xml:lang="en">Each of the accountable elements within a group.</rdfs:comment>
        <rdfs:label xml:lang="en">Item</rdfs:label>
    </owl:Class>
    

    <!-- http://purl.org/iot/vocab/m3-lite#Count -->

    <owl:Class rdf:about="&mthreelite;Count">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Number of available particular things.</rdfs:comment>
        <rdfs:label xml:lang="en">Count</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DoorStatus -->

    <owl:Class rdf:about="&mthreelite;DoorStatus">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Describes if a door is OPEN or CLOSED.</rdfs:comment>
        <rdfs:label xml:lang="en">Door Status</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#CountAvailableBicycles -->

    <owl:Class rdf:about="&mthreelite;CountAvailableBicycles">
        <rdfs:subClassOf rdf:resource="&mthreelite;Count"/>
        <rdfs:comment xml:lang="en">Number of available bicycles at a particular bicycle docking station.</rdfs:comment>
        <rdfs:label xml:lang="en">Count Available Bicycles</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#CountAvailableTaxis -->

    <owl:Class rdf:about="&mthreelite;CountAvailableTaxis">
        <rdfs:subClassOf rdf:resource="&mthreelite;Count"/>
        <rdfs:comment xml:lang="en">Number of taxis available at a particular taxi stop.</rdfs:comment>
        <rdfs:label xml:lang="en">Count Available Taxis</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#CountEmptyDockingPoints -->

    <owl:Class rdf:about="&mthreelite;CountEmptyDockingPoints">
        <rdfs:subClassOf rdf:resource="&mthreelite;Count"/>
        <rdfs:comment xml:lang="en">Number of empty docking points at a particular bicycle docking station.</rdfs:comment>
        <rdfs:label xml:lang="en">Count Empty Docking Points</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#TrafficIntensity -->

    <owl:Class rdf:about="&mthreelite;TrafficIntensity">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">The intensity of a traffic flow is the number of vehicles passing a cross section of a road in a unit of time.</rdfs:comment>
        <rdfs:label xml:lang="en">Traffic Intensity</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ElectricFieldSensor -->

    <owl:Class rdf:about="&mthreelite;ElectricFieldSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Scientific instrument for measuring electromagnetic fields (EMF). Most of them measure the electromagnetic radiation flux density (DC fields) or the change in an electromagnetic field over time (AC fields).</rdfs:comment>
        <rdfs:label xml:lang="en">Electric Field Sensor</rdfs:label>
    </owl:Class>
    
    <!-- http://purl.org/iot/vocab/m3-lite#Counter -->

    <owl:Class rdf:about="&mthreelite;Counter">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensors that reckons occurrences or repetitions of physical objects, phenomena or events.</rdfs:comment>
        <rdfs:label xml:lang="en">Counter</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DoorStateSensor -->

    <owl:Class rdf:about="&mthreelite;DoorStateSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">This sensor detects if a door is in the state of OPEN or CLOSED.</rdfs:comment>
        <rdfs:label xml:lang="en">Door State Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#SoilTemperatureThermometer -->

    <owl:Class rdf:about="&mthreelite;SoilThermometer">
        <rdfs:subClassOf rdf:resource="&mthreelite;Thermometer"/>
        <rdfs:comment xml:lang="en">This sensor reports Soil temperature.</rdfs:comment>
        <rdfs:label xml:lang="en">Soil Thermometer</rdfs:label>
    </owl:Class>
    
    <!-- http://purl.org/iot/vocab/m3-lite#SiemensPerMetre -->

    <owl:Class rdf:about="&mthreelite;SiemensPerMetre">
        <rdfs:subClassOf rdf:resource="&qu;Unit"/>
        <rdfs:comment xml:lang="en">Conductivity is measured in Siemens per metre (S/m).</rdfs:comment>
        <rdfs:label xml:lang="en">Siemens Per Metre, Siemens Per Meter</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#MilligramPerLitre -->

    <owl:Class rdf:about="&mthreelite;MilligramPerLitre">
        <rdfs:subClassOf rdf:resource="&mthreelite;GramPerLitre"/>
        <rdfs:comment xml:lang="en">Level of Dissolved substance in liquid measured in mg per litre.</rdfs:comment>
        <rdfs:label xml:lang="en">Milligram Per Litre, Milligram Per Liter</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#AirHumiditySensor -->

    <owl:Class rdf:about="&mthreelite;AirHumiditySensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;HumiditySensor"/>
        <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource="&mthreelite;hasDomainOfInterest"/>
                <owl:someValuesFrom rdf:resource="&mthreelite;Environment"/>
            </owl:Restriction>
        </rdfs:subClassOf>
        <rdfs:comment xml:lang="en">Sensor used to measure air humidity.</rdfs:comment>
        <rdfs:label xml:lang="en">Air Humidity Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WaterThermometer -->

    <owl:Class rdf:about="&mthreelite;WaterThermometer">
        <rdfs:subClassOf rdf:resource="&mthreelite;Thermometer"/>
        <rdfs:comment xml:lang="en">This sensor reports Water temperature.</rdfs:comment>
        <rdfs:label xml:lang="en">Water Thermometer</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WaterConductivitySensor -->

    <owl:Class rdf:about="&mthreelite;WaterConductivitySensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;ConductivitySensor"/>
        <rdfs:comment xml:lang="en">Device used to measure the conductivity of water.</rdfs:comment>
        <rdfs:label xml:lang="en">Water Conductivity Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WaterPHSensor -->

    <owl:Class rdf:about="&mthreelite;WaterPHSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;PHSensor"/>
        <rdfs:comment xml:lang="en">Device used to detect PH level of water.</rdfs:comment>
        <rdfs:label xml:lang="en">Water PH Sensor</rdfs:label>
    </owl:Class>

        <!-- http://purl.org/iot/vocab/m3-lite#WaterO2IonSensor -->

    <owl:Class rdf:about="&mthreelite;WaterO2IonSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensor used to measure O2 concentration level in the water.</rdfs:comment>
        <rdfs:label xml:lang="en">Water O2 Ion Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WaterNH4IonSensor -->

    <owl:Class rdf:about="&mthreelite;WaterNH4IonSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensor used to measure NH4 concentration level in the water.</rdfs:comment>
        <rdfs:label xml:lang="en">Water NH4 Ion Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WaterNO3IonSensor -->

    <owl:Class rdf:about="&mthreelite;WaterNO3IonSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensor used to measure NO3 concentration level in the water.</rdfs:comment>
        <rdfs:label xml:lang="en">Water NO3 Ion Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#LoRaInterfaceEnergyMeter -->

    <owl:Class rdf:about="&mthreelite;LoRaInterfaceEnergyMeter">
        <rdfs:subClassOf rdf:resource="&mthreelite;EnergyMeter"/>
        <rdfs:comment xml:lang="en">Measure the average power consumption of the LoRa interface nodes.</rdfs:comment>
        <rdfs:label xml:lang="en">LoRa Interface Energy Meter</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#WiFiInterfaceEnergyMeter -->

    <owl:Class rdf:about="&mthreelite;WiFiInterfaceEnergyMeter">
        <rdfs:subClassOf rdf:resource="&mthreelite;EnergyMeter"/>
        <rdfs:comment xml:lang="en">Measure the average power consumption of the WiFi interface nodes.</rdfs:comment>
        <rdfs:label xml:lang="en">WiFi Interface Energy Meter</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#Clock -->

    <owl:Class rdf:about="&mthreelite;Clock">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">sensor that measures time</rdfs:comment>
        <rdfs:label xml:lang="en">Clock</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#HumanPresenceDetector -->

    <owl:Class rdf:about="&mthreelite;HumanPresenceDetector">
        <rdfs:subClassOf rdf:resource="&mthreelite;PresenceDetector"/>
        <rdfs:comment xml:lang="en">Device used to detect if an object (vehicle, room, place, etc.) is occupied by Human.</rdfs:comment>
        <rdfs:label xml:lang="en"> Human Presence Detector</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#VehiclePresenceDetector -->

    <owl:Class rdf:about="&mthreelite;VehiclePresenceDetector">
        <rdfs:subClassOf rdf:resource="&mthreelite;PresenceDetector"/>
        <rdfs:comment xml:lang="en">Device used to detect if an vehicle is present at a place.</rdfs:comment>
        <rdfs:label xml:lang="en"> Vehicle Presence Detector</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentConcentration -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentConcentration">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Measure of Chemical Agent Concentration</rdfs:comment>
        <rdfs:label xml:lang="en">Chemical Agent Concentration Quantity Kind</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentWaterConcentration -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentWaterConcentration">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentConcentration"/>
        <rdfs:comment xml:lang="en">Measure of Chemical Agent Concentration in Water </rdfs:comment>
        <rdfs:label xml:lang="en">Chemical Agent Water Concentration Quantity Kind</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentWaterConcentrationO2 -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentWaterConcentrationO2">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentWaterConcentration"/>
        <rdfs:comment xml:lang="en">Measure of O2 concentration in Water </rdfs:comment>
        <rdfs:label xml:lang="en">Oxygen (O2) Chemical Agent Water Concentration</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentWaterConcentrationNH4Ion -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentWaterConcentrationNH4Ion">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentWaterConcentration"/>
        <rdfs:comment xml:lang="en">Measure of NH4 ion concentration in Water </rdfs:comment>
        <rdfs:label xml:lang="en">Ammonium ion (NH4+) Chemical Agent Water Concentration</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentWaterConcentrationNO3Ion -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentWaterConcentrationNO3Ion">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentWaterConcentration"/>
        <rdfs:comment xml:lang="en">Measure of NO3 ion concentration in Water </rdfs:comment>
        <rdfs:label xml:lang="en">Nitrate Ion (NO3-) Chemical Agent Water Concentration</rdfs:label>
    </owl:Class>

        <!-- http://purl.org/iot/vocab/m3-lite#RadiationParticleDetector -->

    <owl:Class rdf:about="&mthreelite;RadiationParticleDetector">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">A particle detector, also known as a radiation detector or Geiger counter, is a device used to detect, track, and/or identify ionising particles, such as those produced by nuclear decay, cosmic radiation, or reactions in a particle accelerator.</rdfs:comment>
        <rdfs:label xml:lang="en">Radiation Particle Detector</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#IonisingRadiation -->

    <owl:Class rdf:about="&mthreelite;IonisingRadiation">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Ionising radiation is radiation that carries enough energy to free electrons from atoms or molecules, thereby ionizing them. Gamma rays, X-rays, and the higher ultraviolet part of the electromagnetic spectrum are ionizing, whereas the lower ultraviolet part of the electromagnetic spectrum, and also the lower part of the spectrum below UV, including visible light (including nearly all types of laser light), infrared, microwaves, and radio waves are all considered non-ionizing radiation.</rdfs:comment>
        <rdfs:label xml:lang="en">Ionising Radiation</rdfs:label>
    </owl:Class>


    <!-- http://purl.org/iot/vocab/m3-lite#RadiationParticlesPerMinute -->

    <owl:Class rdf:about="&mthreelite;RadiationParticlesPerMinute">
        <rdfs:subClassOf rdf:resource="&qu;Unit"/>
        <rdfs:comment xml:lang="en">The number of ionizing events detected in one minute.</rdfs:comment>
        <rdfs:label xml:lang="en">Radiation Particles Per Minute</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationDust -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationDust">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">Measure of the concentration of dust suspended in the air.</rdfs:comment>
        <rdfs:label xml:lang="en">Chemical Agent Atmospheric Concentration Dust</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DustSensor -->

    <owl:Class rdf:about="&mthreelite;DustSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">A sensing device that measures dust particle concentration.</rdfs:comment>
        <rdfs:label xml:lang="en">Dust Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#OpticalDustSensor -->

    <owl:Class rdf:about="&mthreelite;OpticalDustSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;DustSensor"/>
        <rdfs:comment xml:lang="en">A sensing device that measures dust particle concentration using optical sensing mean.</rdfs:comment>
        <rdfs:label xml:lang="en">Optical Dust Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#Voltage -->

    <owl:Class rdf:about="&mthreelite;Voltage">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">An electromotive force or potential difference expressed in volts (Source Google).</rdfs:comment>
        <rdfs:label xml:lang="en">Voltage</rdfs:label>
    </owl:Class>


    <!-- V3 updates-->



	<!-- http://purl.org/iot/vocab/m3-lite#VOCSensor -->

    <owl:Class rdf:about="&mthreelite;VOCSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;GaseousPollutantSensor"/>
        <rdfs:comment xml:lang="en">Sensor that detects levels of Volatile Organic Components (VOC) in the environment.</rdfs:comment>
        <rdfs:label xml:lang="en">Volatile Organic Compound (VOC) Sensor</rdfs:label>
    </owl:Class>

	<!-- http://purl.org/iot/vocab/m3-lite#IEEE802154InterfaceEnergyMeter -->

    <owl:Class rdf:about="&mthreelite;IEEE802154InterfaceEnergyMeter">
        <rdfs:subClassOf rdf:resource="&mthreelite;EnergyMeter"/>
        <rdfs:comment xml:lang="en">Sensor that measures the power consumption of the IEEE802.15.4 interface nodes.</rdfs:comment>
        <rdfs:label xml:lang="en">IEEE802.15.4 Interface Energy Meter</rdfs:label>
    </owl:Class>

	<!-- http://purl.org/iot/vocab/m3-lite#BoardVoltageSensor -->

    <owl:Class rdf:about="&mthreelite;BoardVoltageSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;VoltageSensor"/>
        <rdfs:comment xml:lang="en">Sensor used to measure board input voltage.</rdfs:comment>
        <rdfs:label xml:lang="en">Board Voltage Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DeviceUptimeClock -->

    <owl:Class rdf:about="&mthreelite;DeviceUptimeClock">
        <rdfs:subClassOf rdf:resource="&mthreelite;Clock"/>
        <rdfs:comment xml:lang="en">Sensor that measures time a device is working and is available.</rdfs:comment>
        <rdfs:label xml:lang="en">Device Uptime Clock</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#VoiceCommandSensor -->

    <owl:Class rdf:about="&mthreelite;VoiceCommandSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensor that uses automatic speech recognition technology to match or reject a recorded voice command according to a specified set of available voice commands.</rdfs:comment>
        <rdfs:label xml:lang="en">Voice Command Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DirectionOfArrivalSensor -->

    <owl:Class rdf:about="&mthreelite;DirectionOfArrivalSensor">
        <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
        <rdfs:comment xml:lang="en">Sensor that estimates the azimuth direction of sources relative to the sensor’s position.</rdfs:comment>
        <rdfs:label xml:lang="en">Direction Of Arrival (DOA) Sensor</rdfs:label>
    </owl:Class>


    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationNO -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationNO">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">Measure of the concentration of Carbon Monoxide (CO) gas suspended in the atmosphere.</rdfs:comment>
        <rdfs:label xml:lang="en">Nitrogen Monoxide (NO) Chemical Agent Atmospheric Concentration</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationSO2 -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationSO2">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">Measure of the concentration of Sulphur dioxide (SO2) gas suspended in the atmosphere.</rdfs:comment>
        <rdfs:label xml:lang="en">Sulphur dioxide (SO2) Chemical Agent Atmospheric Concentration</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationVOC -->

    <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationVOC">
        <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">Measure of the concentration of Volatile Organic Compound gas suspended in the atmosphere.</rdfs:comment>
        <rdfs:label xml:lang="en">Volatile Organic Compound Chemical Agent Atmospheric Concentration</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#ChemicalAgentAtmosphericConcentrationNH3 -->
     
     <owl:Class rdf:about="&mthreelite;ChemicalAgentAtmosphericConcentrationNH3">
         <rdfs:subClassOf rdf:resource="&mthreelite;ChemicalAgentAtmosphericConcentration"/>
        <rdfs:comment xml:lang="en">Measure of the concentration of Ammonia (NH3) gas suspended in the atmosphere.</rdfs:comment>
        <rdfs:label xml:lang="en">Ammonia (NH3) Chemical Agent Atmospheric Concentration</rdfs:label>
    </owl:Class>

	<!-- http://purl.org/iot/vocab/m3-lite#DeviceUptime -->

    <owl:Class rdf:about="&mthreelite;DeviceUptime">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Time a device is working and is available</rdfs:comment>
        <rdfs:label xml:lang="en">Device Uptime</rdfs:label>
    </owl:Class>
    
    <!-- http://purl.org/iot/vocab/m3-lite#VoiceCommand -->

    <owl:Class rdf:about="&mthreelite;VoiceCommand">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">A voice command to control a voice controlled system or environment, such as a smart home.</rdfs:comment>
        <rdfs:label xml:lang="en">Voice Command</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#DirectionOfArrival -->

    <owl:Class rdf:about="&mthreelite;DirectionOfArrival">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">The azimuth direction of a  source relative to the azimuth direction of the DOA sensor.</rdfs:comment>
        <rdfs:label xml:lang="en">Direction Of Arrival</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#PartsPerBillion -->

    <owl:Class rdf:about="&mthreelite;PartsPerBillion">
        <rdfs:subClassOf rdf:resource="&mthreelite;PPM"/>
        <rdfs:comment xml:lang="en">It describes the concentration of something in parts per billion parts of water or soil, expressed in any (but common) unit of measurement.</rdfs:comment>
        <rdfs:label xml:lang="en">Parts Per Billion</rdfs:label>
    </owl:Class>
	
	<!-- http://purl.org/iot/vocab/m3-lite#DecibelMilliwatt -->

    <owl:Class rdf:about="&mthreelite;DecibelMilliwatt">
        <rdfs:subClassOf rdf:resource="&mthreelite;Decibel"/>
        <rdfs:comment xml:lang="en">It describes power ratio in decibels of the measured power referenced to one milliwatt.</rdfs:comment>
        <rdfs:label xml:lang="en">Decibel Milliwatt</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#VoiceCommandController -->

    <owl:Class rdf:about="&mthreelite;VoiceCommandController">
        <rdfs:subClassOf rdf:resource="&iot-lite;ActuatingDevice"/>
        <rdfs:comment xml:lang="en">An actuating device called  that allows to semi-control the environment of the Voice Command Sensor.</rdfs:comment>
        <rdfs:label xml:lang="en">Voice Command Controller</rdfs:label>
    </owl:Class>

 <!-- http://purl.org/iot/vocab/m3-lite#OxidationReductionPotentialSensor -->
  
      <owl:Class rdf:about="&mthreelite;OxidationReductionPotentialSensor">
         <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
         <rdfs:comment xml:lang="en">Measures the Water Oxidation Reduction Potential (ORP) as the tendency of a chemical species  to acquire electrons and thereby be reduced.</rdfs:comment>
         <rdfs:label xml:lang="en">Oxidation Reduction Potential (ORP) Sensor</rdfs:label>
    </owl:Class>


    <!--  http://purl.org/iot/vocab/m3-lite#CountPeople -->

    <owl:Class rdf:about="&mthreelite;CountPeople">
        <rdfs:subClassOf rdf:resource="&mthreelite;Count"/>
        <rdfs:comment xml:lang="en">Number of people within a particular area</rdfs:comment>
        <rdfs:label xml:lang="en">Count People</rdfs:label>
    </owl:Class>

    <!--  http://purl.org/iot/vocab/m3-lite#PeopleCountSensor -->

    <owl:Class rdf:about="&mthreelite;PeopleCountSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;Counter"/>
        <rdfs:comment xml:lang="en">Device used to count the number of people (eg., used within an indoor area).</rdfs:comment>
        <rdfs:label xml:lang="en">People Count Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#TimeOfArrival -->

    <owl:Class rdf:about="&mthreelite;TimeOfArrival">
        <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
        <rdfs:comment xml:lang="en">Estimated time till an object is arriving to a specific location (typically used in transportation).</rdfs:comment>
        <rdfs:label xml:lang="en">Time of arrival</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#TimeOfArrivalSensor -->

    <owl:Class rdf:about="&mthreelite;TimeOfArrivalSensor">
    <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
    <rdfs:comment xml:lang="en">Virtual device that estimates the remaining time for an object to arrive at a specific location.</rdfs:comment>
    <rdfs:label xml:lang="en">Time of Arrival Sensor</rdfs:label>
</owl:Class>


<!-- http://purl.oclc.org/NET/ssnx/ssn#Platform -->

<owl:Class rdf:about="&ssn;Platform">
    <dc:source>skos:exactMatch &apos;platform&apos; [SensorML OGC-0700]
                                  http://www.opengeospatial.org/standards/sensorml</dc:source>
                              <rdfs:comment>An Entity to which other Entities can be attached - particularly Sensors and other Platforms.  For example, a post might act as the Platform, a buoy might act as a Platform, or a fish might act as a Platform for an attached sensor.</rdfs:comment>
                              <rdfs:label xml:lang="en">Platform</rdfs:label>
                              <rdfs:seeAlso>http://www.w3.org/2005/Incubator/ssn/wiki/SSN_Deploy#PlatformSite</rdfs:seeAlso>
                          </owl:Class>


                          <!-- http://purl.org/iot/vocab/m3-lite#Bus   -->

                          <owl:Class rdf:about="&mthreelite;Bus">
                              <rdfs:subClassOf rdf:resource="&ssn;Platform"/>
                              <rdfs:comment xml:lang="en">A large motor vehicle carrying passengers by road, typically one serving the public on a fixed route and for a fare.</rdfs:comment>
                              <rdfs:label xml:lang="en">Bus</rdfs:label>
                          </owl:Class>


                          <!-- http://purl.org/iot/vocab/m3-lite#LoRaInterface -->

                          <owl:Class rdf:about="&mthreelite;LoRaInterface">
                              <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
                              <rdfs:comment xml:lang="en">This device is used for long range low power wireless communications. We can use this device to report network metrics (SNR, latence, etc.)</rdfs:comment>
                              <rdfs:label xml:lang="en">LoRa Interface</rdfs:label>
                          </owl:Class>

                          <!-- http://purl.org/iot/vocab/m3-lite#SNR -->

                          <owl:Class rdf:about="&mthreelite;SNR">
                              <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
                              <rdfs:comment xml:lang="en">Compares the level of a desired signal to the level of noise. </rdfs:comment>
                              <rdfs:label xml:lang="en">Signal to Noise Ratio</rdfs:label>
                          </owl:Class>

                          <!-- http://purl.org/iot/vocab/m3-lite#RSSI -->

                          <owl:Class rdf:about="&mthreelite;RSSI">
                              <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
                              <rdfs:comment xml:lang="en">Received Signal Strength Indicator is the signal strength in a wireless network environment.</rdfs:comment>
                              <rdfs:label xml:lang="en">Received Signal Strength Indicator</rdfs:label>
                          </owl:Class>

                          <!-- http://purl.org/iot/vocab/m3-lite#HDOP -->

                          <owl:Class rdf:about="&mthreelite;HDOP">
                              <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
                              <rdfs:comment xml:lang="en">Describes the preceision of the GPS signal. Smaller the value is, more precise the signal is.</rdfs:comment>
                              <rdfs:label xml:lang="en">Horizontal Dilution of Precision</rdfs:label>
                          </owl:Class>

                          <!--  http://purl.org/iot/vocab/m3-lite#CountStatellitesSignalReceived -->

                          <owl:Class rdf:about="&mthreelite;CountStatellitesSignalReceived">
                              <rdfs:subClassOf rdf:resource="&mthreelite;Count"/>
                              <rdfs:comment xml:lang="en">Count of satellites from which a signal is received</rdfs:comment>
                              <rdfs:label xml:lang="en">Count Statellites Signal Received</rdfs:label>
                          </owl:Class>

                          <!--  http://purl.org/iot/vocab/m3-lite#CurrentSensor -->

                          <owl:Class rdf:about="&mthreelite;CurrentSensor">
                              <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
                              <rdfs:comment xml:lang="en">A current sensor is a device that detects electric current in a wire, and generates a signal proportional to that current.</rdfs:comment>
                              <rdfs:label xml:lang="en">Current Sensor</rdfs:label>
                          </owl:Class>
                      
                          <!-- http://purl.org/iot/vocab/m3-lite#TimeOfArrivalNextBus -->
                          <owl:Class rdf:about="http://purl.org/iot/vocab/m3-lite#TimeOfArrivalNextBus">
                            <rdfs:subClassOf rdf:resource="http://purl.org/iot/vocab/m3-lite#TimeOfArrival"/>
                            <rdfs:comment xml:lang="en">Estimated time (in seconds) till a bus will arrive at a specific bus stop. </rdfs:comment>
                            <rdfs:label xml:lang="en">Time of arrival for the next bus</rdfs:label>
                         </owl:Class>

                         <!--http://purl.org/iot/vocab/m3-lite#TimeOfArrivalNextBusSensor -->
                         <owl:Class rdf:about="http://purl.org/iot/vocab/m3-lite#TimeOfArrivalNextBusSensor">
                              <rdfs:subClassOf rdf:resource="http://purl.org/iot/vocab/m3-lite#TimeOfArrivalSensor"/>
                                                  <rdfs:comment xml:lang="en">Virtual device that represents a bus stop X and a bus line Y; it estimates the remaining time the next bus (belonging to line Y) will arrive at bus stop X. </rdfs:comment>
                                                      <rdfs:label xml:lang="en">Time of arrival sensor for the next bus</rdfs:label>
                                                  </owl:Class>

                                                  <!-- http://purl.org/iot/vocab/m3-lite#DistanceNextBus -->
                                                  <owl:Class rdf:about="http://purl.org/iot/vocab/m3-lite#DistanceNextBus">
                                                          <rdfs:subClassOf rdf:resource="http://purl.org/iot/vocab/m3-lite#Distance"/>
                                                              <rdfs:comment xml:lang="en">Estimated driving distance (in meters) between a bus and the next bus stop. </rdfs:comment>
                                                                  <rdfs:label xml:lang="en">Driving distance between a bus and the next bus stop</rdfs:label>
                                                              </owl:Class>

<!-- http://purl.org/iot/vocab/m3-lite#DistanceNextBusSensor -->
    <owl:Class rdf:about="http://purl.org/iot/vocab/m3-lite#DistanceNextBusSensor">
        <rdfs:subClassOf rdf:resource="http://purl.org/iot/vocab/m3-lite#DistanceSensor"/>
        <rdfs:comment xml:lang="en"> Virtual device that represents a bus stop X and a bus line Y; it measures the driving distance between the next bus (belonging to line Y) and the bus stop X. </rdfs:comment>
        <rdfs:label xml:lang="en">Sensor measuring the driving distance between a bus and the next bus stop</rdfs:label>
    </owl:Class>


    <!-- http://purl.org/iot/vocab/m3-lite#StayingPeopleCountSensor -->
    <owl:Class rdf:about="&mthreelite;StayingPeopleCountSensor">
        <rdfs:subClassOf rdf:resource="&mthreelite;PeopleCountSensor"/>
        <rdfs:comment xml:lang="en">Device used to count the number of people that stays within an area for more than a threshold of time.</rdfs:comment>
        <rdfs:label xml:lang="en">Staying People Count Sensor</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#CountPeopleStaying -->

    <owl:Class rdf:about="&mthreelite;CountPeopleStaying">
        <rdfs:subClassOf rdf:resource="&mthreelite;CountPeople"/>
        <rdfs:comment xml:lang="en">Number of people that stayed within an area for more than a threshold of time.</rdfs:comment>
        <rdfs:label xml:lang="en">Count People Staying</rdfs:label>
    </owl:Class>

    <!-- http://purl.org/iot/vocab/m3-lite#PeopleStayDurationSensor  -->

    <owl:Class rdf:about="&mthreelite;PeopleStayDurationSensor">
         <rdfs:subClassOf rdf:resource="&ssn;SensingDevice"/>
         <rdfs:comment xml:lang="en">Device used to measure the time people stays within an area.</rdfs:comment>
         <rdfs:label xml:lang="en">People Stay Duration Sensor</rdfs:label>
     </owl:Class>

     <!-- http://purl.org/iot/vocab/m3-lite#PeopleStayDurationAverage  -->

     <owl:Class rdf:about="&mthreelite;PeopleStayDurationAverage">
         <rdfs:subClassOf rdf:resource="&qu;QuantityKind"/>
         <rdfs:comment xml:lang="en">Average time people stays within an area.</rdfs:comment>
         <rdfs:label xml:lang="en">People Stay Duration Average </rdfs:label>
     </owl:Class>

     <!-- http://purl.org/iot/vocab/m3-lite#PeopleFlowCountSensor -->

     <owl:Class rdf:about="&mthreelite;PeopleFlowCountSensor">
         <rdfs:subClassOf rdf:resource="&mthreelite;PeopleCountSensor"/>
         <rdfs:comment xml:lang="en">Device used to count the number of people the moves from an area towards another area during a time window.</rdfs:comment>
         <rdfs:label xml:lang="en">People Flow Count Sensor</rdfs:label>
     </owl:Class>

     <!-- http://purl.org/iot/vocab/m3-lite#CountPeopleMoving -->

     <owl:Class rdf:about="&mthreelite;CountPeopleMoving">
         <rdfs:subClassOf rdf:resource="&mthreelite;CountPeople"/>
         <rdfs:comment xml:lang="en">Number of people that moved outside an area towards another area during a time window.</rdfs:comment>
         <rdfs:label xml:lang="en">Count People Moving</rdfs:label>
     </owl:Class>
'''

# pattern = r'(<owl:Class rdf:about="[^"]+">.*?</owl:Class>)'
# matches = re.findall(pattern, text, re.DOTALL)
# print(matches)
# for match in matches:
#     print(match.strip())
#
# # 定义列表
#
#
# # 将列表转换为字符串
# list_str = ', '.join(matches)
#
# # 指定文件路径
# file_path = 'C:\\Users\\20245\\Desktop\\Scientific Research\\ontology.txt'  # 替换为你想要保存文件的完整路径
#
# # 打开文件并写入字符串
# with open(file_path, 'w') as file:
#     file.write(list_str)

