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
This Plugin enables you to import ER Model into Astah models by Excel file.

Features
--------------------
* ExcelファイルからのERエンティティモデルのインポート
 指定されたExcelファイルに定義された以下の項目を、AstahのERエンティティモデルとしてインポートできます。

 * Entity
  * Logical Name
  * Physical Name
 * Attribute
  * Logical Name
  * Physical Name
  * Primary Key(指定されたセルに、何かしらの値が設定されている場合に、Primary Keyとして設定されます) 
  * NOT NULL(指定されたセルに、何かしらの値が設定されている場合に、NOT NULLとして設定されます) 
  * Default Value
  * Data Type(存在しないData Typeは指定できません。Astahが用意していないデータ型を利用する場合は、事前に"Tool-ER Diagram-Set ER Data Type"メニューより、Data Typeを追加してください)
  * Length/Precision
  * Definition

 また、Excelで指定された属性の"論理名、物理名、データ型、長さ(精度)、NOT NULL"が一致する、ERドメインモデルが現在開いているモデルに存在する場合、そのERドメインが属性のドメインとして利用されます。

* ExcelファイルからのERドメインモデルのインポート
 指定されたExcelファイルの最初のシートに定義された以下の項目を、AstahのERドメインモデルとしてインポートできます。

 * Logical Name
 * Physical Name
 * Data Type
 * Definition

How to install
----------------
0. [Download the jar file.](http://cdn.change-vision.com/plugins/excel2er-1.0.0.jar)
1. Start Astah
2. Install the plug-in at the Plugin List Dialog ([help]-[Plugin List] menu)
3. You find that the [Excel-ERModel Import] has been added under the [Tool] - [ER Diagram] menu

How to build
------------
1. Install the Astah Plug-in SDK - <http://astah.net/features/sdk>
2. `git clone git://github.com/ChangeVision/astah-excel2er-plugin.git`
3. `cd astah-excel2er-plugin`
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

