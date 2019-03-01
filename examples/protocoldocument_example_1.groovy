/**
 * Create a new docBeeDocument with planning time by finish a protocolDocument
 */

//Placeholder name of protocol entry of type SELECTION_CATEGORY
String userSelectionProtocolPlaceholderName = "USER_SELECTION"
//Placeholder name of protocol entry of type DATE
String planingTimeDateProtocolPlaceholderName = "PLANNING_DATE"
//Placeholder name of protocol entry of type TIME
String planingTimeTimeProtocolPlaceholderName = "PLANNING_TIME"
//Placeholder name of protocol entry of type DURATION
String planingTimeEstimateProtocolPlaceholderName = "PLANNING_ESTIMATE"
//Placeholder name of protocol entry of type MULTILINE_TEXT
String descriptionProtocolPlaceholderName = "DESCRIPTION"
//ServiceType name
String serviceTypeName = "Default"

if ( protocolDocument ) {
  //Get selection value from protocol for entry with placeholder
  SelectionValue selectionValue = (SelectionValue)protocolDocument.findEntryByPlaceholder(userSelectionProtocolPlaceholderName).getValueAsSelectionValue()
  if ( !selectionValue ) {
    println("No selection value found")
    return
  }

  User user = app.findUserByName(selectionValue.getName())
  if ( !user ) {
    println("No user found")
    return
  }

  Long planingDate = (Long)protocolDocument.findEntryByPlaceholder(planingTimeDateProtocolPlaceholderName).getValueAsLong()
  Long planingTime = (Long)protocolDocument.findEntryByPlaceholder(planingTimeTimeProtocolPlaceholderName).getValueAsLong()
  Long planingEstimate = (Long)protocolDocument.findEntryByPlaceholder(planingTimeEstimateProtocolPlaceholderName).getValueAsLong()

  if ( !planingDate || !planingTime || !planingEstimate ) {
    println("Not all planing time values are set")
    return
  }

  DocBeeDocument docBeeDocument = app.createDocBeeDocument(protocolDocument.getCustomer(), protocolDocument.getCustomerLocation(), protocolDocument.getCustomerContact(), user)
  if ( !docBeeDocument )  {
    println("DocBeeDocument not created")
    return
  }

  ServiceType serviceType = app.findServiceTypeByName(serviceTypeName)
  if ( !serviceType ) {
    println("No service type found")
    return
  }

  //find the optional description text
  String description = (String)protocolDocument.findEntryByPlaceholder(descriptionProtocolPlaceholderName)?.getValueAsString()
  //Add new task
  if ( docBeeDocument.addTask(serviceType.getName(), description, serviceType) ) {
    //Get the first task
    Task task = docBeeDocument.getTasks()?.first()
    if ( task ) {
      //Add user as worker
      task.addWorker(user)
      //Add work planning time
      task.addPlaningTime("WORK", planingDate + planingTime, planingEstimate)
    }
  }
  else {
    println("Task not created")
  }
}
