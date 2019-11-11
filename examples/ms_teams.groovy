/**
 * Create a new message in ms teams 
 */

String url = "" //Add your MS Teams Webhook URL
String docBeeBaseUrl = "" //Add your DocBee base url for example https://my.docbee.com/

if ( ticket ) {

  String summary = "[#"+ticket.getTicketNumber()+"]"
  String personInCharge = ticket.getOwner()?.getName()
  if ( !personInCharge ) {
    personInCharge = "-"
  }

  String statusName = ticket.getStatus().getName()
  String customerName = ticket.getCustomer()?.getName()
  if ( !customerName ) {
    customerName = "-"
  }

  String ticketLink = docBeeBaseUrl + "ticket/edit/" + ticket.getDocBeeId()


  String activityTitle = "[#"+ticket.getTicketNumber()+"]"
  if ( ticket.getDescription() ) {
    String[] splittedDescription = ticket.getDescription().split("\n")
    activityTitle += " " + splittedDescription[0]
  }
  String activitySubtitle = "Neuer Vorgang"

  Map body = [
      "@type": "MessageCard",
      "@context": "http://schema.org/extensions",
      "themeColor": "EE7F01",
      "summary": summary,
      "sections": [
          [
            "activityTitle": activityTitle,
            "activitySubtitle": activitySubtitle,
            "facts": [
              [
                "name": "Kunde",
                "value": customerName
              ],
              [
                "name": "Verantwortlicher",
                "value": personInCharge
              ],
              [
                "name": "Status",
                "value": statusName
              ]
            ],
            "markdown": true
          ]
      ],
      "potentialAction": [
          [
            "@type": "OpenUri",
            "name": "In DocBee Öffnen",
            "targets": [
              [
                "os": "default",
                "uri": ticketLink
              ]
            ]
          ]
      ]
  ]

  HttpClient http = app.getHttpClient()
  HttpResult result = http.post(url, body, (Map)null, (Map)null)
}
