import mongoose from "mongoose";
import Employee from "./models/employee.js";

async function run() {
    // Connect to MongoDB
    await mongoose.connect("mongodb://localhost:27017/mongoose_database");
    console.log("Connected to MongoDB.");

    // Clean existing data
    await Employee.deleteMany({});

    // Insert Joe Doe
    const joeDoe = await Employee.create({
      name: "Joe Doe",
      age: 45,
      manager: null,
      emails: ["asd@email.com", "asd@email.cz"],
      salary: 45000,
      address: {
        street: "Heatrow 1",
        city: "NYC",
      },
    });

    console.log("Joe Doe saved!");
  
    // Insert Jane Doe
    const janeDoe = await Employee.create({
      name: "Jane Doe",
      age: 31,
      manager: joeDoe._id,
      emails: ["jane@email.com", "jane2@email.cz"],
      salary: 41000,
      address: {
        street: "Dlouha 25",
        city: "Praha",
      },
    });

    console.log("Jane Doe saved!");
    
    // Mixed Types
    const joeDoeNothing1 = await Employee.create({
      name: "Joe Doe Mixed Nothing1",
      age: "thirty one",
      manager: null,
    });

    const joeDoeNothing2 = await Employee.create({
      name: "Joe Doe Mixed Nothing2",
      age: null,
      manager: null,
    });

    const joeDoeNothing3 = await Employee.create({
      name: "Joe Doe Mixed Nothing3",
      manager: null
    });

    const joeDoeNothing4 = await Employee.create({
      name: "Joe Doe Mixed Nothing4",
      manager: null,
      emails: ["abc@email.com", 123]
    });

    // Querying Mixed Types and Null Representation

    //case 1 & 2: same key, different data types and missing keys 

    const youngest = await Employee.find().sort({ age: 1 });
    console.log("Employees by age:", youngest.map((emp) => emp.name));
    
    const age31 = await Employee.find({ age: 31 });
    console.log("age31 employee:", age31.map((emp) => emp.name));
    
    // case 3: Heterogeneous array
    const emails = await Employee.find({ emails: "abc@email.com" });
    console.log("emails employee:", emails.map((emp) => emp.name));
    

    
    /*
    // Query youngest employee
    const youngest = await Employee.findOne().sort({ age: 1 });
    console.log("Youngest employee:", youngest.name);
    
    // Query employees living in NYC
    const employeesInNYC = await Employee.find({ "address.city": "NYC" });

    employeesInNYC.forEach((e) => {
      console.log("Employees living in NYC:", e.name);
    });

  
    // Delete Joe & Jane
    await Employee.deleteMany({
      name: { $in: ["Joe Doe", "Jane Doe"] },
    });

    console.log("Joe Doe and Jane Doe deleted!");

    */

    // Close connection
    await mongoose.disconnect();
}

run().catch((err) => console.error(err));
