# Telex Airbyte Integration Tool
Airbyte is arguably the best open-source data integration tool available today. It is a powerful and flexible tool that can be used to move data from a wide variety of sources to a wide variety of destinations. Telex provides the platform to integrate Airbyte and post sync data to channels.

## Features
- **Data Integration**: Airbyte is a powerful and flexible tool that can be used to move data from a wide variety of sources to a wide variety of destinations.
- **Data Transformation**: Telex provides the platform to transform data from Airbyte and post data to Telex channels.
- **Data Monitoring**: Telex provides the platform to monitor data from Airbyte.
- **Data Alerting**: Telex provides the platform to alert data from Airbyte.

## Tools & Technologies
> - Airbyte
> - Telex
> - Java 21
> - Spring Boot
> - Heroku
> - Jackson
> - Maven
> - Git
> - GitHub

## Installation & Setup
1. **Clone the repository**
```bash
git clone git@github.com:HNG-12/telex-airbyte-integration.git
```
2. **Change directory**
```bash
cd telex-airbyte-integration
```
3. **Run the application**
```bash
mvn spring-boot:run
```

## Usage
### **Create a new Airbyte connection**
1. **Open the Airbyte UI**
Sign up or log in to the Airbyte UI.
```bash
https://cloud.airbyte.com/
```
2. **Create a new connection**
Click on the `Connections` tab and then click on the `New Connection` button.
```bash
* Define the source - Click on Set up a new source
* Select a Source from the list of Suggested sources e.g. `Google Sheets`
* Configure the source - Enter the necessary details
* Define the destination - Click on Set up a new destination
* Select a Destination from the list of Suggested destinations e.g. `Postgres`
* Configure the destination - Enter the necessary details
* Schedule the sync - Set the frequency of the sync
* Save the connection
```
## **Setup Channel in Telex**
1. **Open the Telex UI**
Sign up or log in to the Telex UI.
```bash
https://telexim
```
2. **Create an organization**
3. **Create a channel**
```bash
* Click on the `Channels` tab and then click on the `+` button.
* Enter the necessary details and save the channel.
* Copy the channel webhook URL.
```
4. **Activate the Airbyte Integration in Telex Apps**
```bash
* Find Airbyte Integration in the list of available integrations.
* Click on the `Activate` button.
* Click on `Manage App`.
* Click on `Settings tab`.
* Enter the `channel` webhook URL.
* Save the settings.
```
![Manage App](https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/airbyte_manage_app.png)

## **Setup Airbyte Webhook**
1. **Open the Airbyte UI**
2. **Click on Settings tab**
3. **Click on Notifications**
4. **Activate Webhook and enter the Webhook Url**
```bash
https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/webhook/airbyte
```
5. **Save the settings**

![Telex Airbyte Integration](https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/airbyte_input_webhook.png)

## **Run the connection**
1. **Open the Airbyte UI**
2. **Click on the connection**
3. **Click on the `Sync now` button**

![Airbyte Sync](https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/airbyte_postgres_sync.png)

![Airbyte Sync Complete](https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/airbyte_postgres_sync_complete.png)

## **Check the Telex channel for the data**
When the sync is complete, the data will be posted to the Telex channel.
![Telex Channel](https://telex-airbyte-integration-93ef60b1a5d1.herokuapp.com/airbyte_sync_success.png)

## **Author**
[Aniebiet Afia](https://github.com/aniebietafia)