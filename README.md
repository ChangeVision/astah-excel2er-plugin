Excel-ERModel Import Plugin
===============================

Version
----------------
1.0.0

Available for
----------------
Astah Professional 6.6.8 or later

Description
----------------
This Plugin enables you to import items on Excel into Astah as ER models.

Features
--------------------
* Import ER Entity Models via Excel file
   Following items on Excel file can be imported as ER Entity models into Astah Professional (http://astah.net/editions/professional).
   
 * Entity
  * Logical Name
  * Physical Name
 * Attribute
  * Logical Name
  * Physical Name
  * Primary Key (When any value exists in the cell specified as Primary Key)
  * NOT NULL (When any value exists in the cell specified as Not NULL) 
  * Default Value
  * Data Type (Unknown Data Type cannot be specified. Please make sure that all the required Data Type is already added into Astah in advance from [Tool] - [ER Diagram] - [Set ER Data Type])
  * Length/Precision
  * Definition

In case there is already an existing ER Domain Model that matches with the information of specified Attribute in Excel "Logical Name, Physical Name, Data Type, Length/Precision and NOT NULL", its existing ER Domain will be used as the Attribute's Domain.

* Import ER Domain models via Excel file
 Following items on Excel file can be imported as ER Domain models into Astah Professional (http://astah.net/editions/professional)

 * Logical Name
 * Physical Name
 * Data Type
 * Definition

How to install
----------------
0. [Download the jar file.](http://cdn.change-vision.com/plugins/excel2er-1.0.0.jar)
1. Start Astah Professional
2. Install the plug-in at the Plugin List Dialog ([help]-[Plugin List] menu)
3. You find that the [Import ER models from Excel] has been added under the [Tool] - [ER Diagram] menu

How to build
------------
1. Install the Astah Plug-in SDK - <http://astah.net/features/sdk>
2. `git clone git://github.com/ChangeVision/astah-excel2er-plugin.git`
3. `cd excel2er`
4. `astah-build`
5. `astah-launch`

License
---------------
Copyright 2014 Change Vision, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

   <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
