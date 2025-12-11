// this stupid import is the only way how I was able to use the Ottoman.js
import {} from './models/db.js';

import { Employee } from './models/employee.js';

import ottoman from 'ottoman';

async function run() {
    // Employee
    console.log(Employee);

    // Remove all
    await Employee.removeMany({})

    const joeDoe = new Employee({
        name: "Joe Doe",
        age: 45,
        manager: null,
        emails: ["asd@email.com", "asd@email.cz"],
        salary: 45000,
        address: { street: "Heatrow 1", city: "NYC" },
    });

    await joeDoe.save();
    console.log("Joe Doe saved!");
  
    // INSERT Jane Doe
    const janeDoe = new Employee({
        name: "Jane Doe",
        age: 31,
        manager: joeDoe,
        emails: ["jane@email.com", "jane2@email.cz"],
        salary: 41000,
        address: { street: "Dlouha 25", city: "Praha" },
    });

    await janeDoe.save();
    console.log("Jane Doe saved!");

    /*

    // Mixed Types and null representation
    const joeDoeMixedNull = new Employee({
        name: "Joe Doe forty five",
        age: "forty five",
        manager: null
    });

    await joeDoeMixedNull.save();
    console.log("joeDoeMixedNull saved!")

    // Mixed Types
    const joeDoeMixedNull1 = await Employee.create({
      name: "Joe Doe Mixed1",
      age: "thirty one",
      manager: null,
    });
    await joeDoeMixedNull1.save();

    const joeDoeMixedNull2 = await Employee.create({
      name: "Joe Doe Mixed2",
      age: null,
      manager: null,
    });
    await joeDoeMixedNull2.save();

    const joeDoeMixedNull3 = await Employee.create({
      name: "Joe Doe Mixed3",
      manager: null
    });
    await joeDoeMixedNull3.save();

    const joeDoeMixedNull4 = await Employee.create({
      name: "Joe Doe Mixed4",
      manager: null,
      emails: ["abc@email.com", 123]
    });
    await joeDoeMixedNull4.save();
    */
    
    // Querying Mixed Types and Null Representation

    //case 1 & 2: same key, different data types and missing keys 
    
    const youngestEmployees = await Employee.find({}, { sort: { age: "ASC" } });
    console.log("Employees by age:", youngestEmployees.rows.map((emp) => emp.name));
    
    
    const age31 = await Employee.find({ age: 31 });
    console.log("age31 employee:", age31.rows.map((emp) => emp.name));
    
    
    // case 3: Heterogeneous array
    const emails = await Employee.find({ emails: {$in: ["jane@email.com"]} });
    console.log(emails)
    console.log("emails employee:", emails.rows.map((emp) => emp.name));
    
    console.log("Employee is using collection:", Employee.collection.name);




    /*

    
    // QUERY: Youngest employee
    const youngest = await Employee.find({}, { sort: { age: "ASC" }, limit: 1 });
    console.log("Youngest employee:", youngest.rows[0].name);

    // QUERY: Employees living in NYC
    const employeesInNYC = await Employee.find({
    'address.city': 'NYC'
    });

    for (const row of employeesInNYC.rows) {
        console.log("Employees living in NYC:", row.name);
    }

    // DELETE Joe & Jane
        await Employee.removeMany({
            name: { $in: ["Joe Doe", "Jane Doe"] }
        });
    console.log("Joe Doe and Jane Doe deleted!");
    */
}

run().catch(err => console.error(err));