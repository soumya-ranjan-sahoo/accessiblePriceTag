# Accessible PriceTag
This project has the goal to make price tags and product information easily accessible for visually impaired users. 

Price tags are augmented with sensors (e.g. capacitive touch) such that users can touch the price tags to get auditive feedback about
its price and the product itself via the users’ personal smartphone.

### Peripherials

1. Wemos Mini D1 ESP32 - Sensing the touch Event.
2. Rpi 3 - Used as a WAP for spanning a local wireless network and acts a server in the system.
3. BLE Beacons - Used to determine the distance between the pricetag and the android device. 
3. Android Devices - Runs an android app for auditive feedback about the product's price.

### High level Workflow 

The Rpi server spans its own wireless network to which all the wemos minis and android devices are connected. Upon a touch event (price tag being touched), a wemos mini senses the event and forwards it to the Rpi server which in turn broadcasts the event to all the connected android devices. The devices which are within the proximity of the touched pricetag sends an acknowledgment to the server, to which the server responds by sending the price tag details. The device finally provides an auditive feedback of the price tag details with enlarged
text information on the android application.

## Prerequisites 

Set up a Rpi as a WAP to span its own wireless network (WiFi). In our case, Rpi runs on a Raspian OS. Install the follwoing packages with the given instructions on your Rpi.

### Installation

Use the package utility apt-get to install [hostapd](https://w1.fi/hostapd/) and [dnsmasq](http://thekelleys.org.uk/dnsmasq/docs/dnsmasq-man.html)

```bash
sudo apt-get -y install hostapd dnsmasq
```

Hostapd is a program that allows you to use the WiFi radio as an access point, and Dnsmasq is a lightweight combination of DHCP and DNS services.


Edit the dhcpcd file:

```bash
sudo nano /etc/dhcpcd.conf
```

To the bottom of the file, add:
```bash
denyinterfaces wlan0
```
Open the interfaces file with the following command:
```bash
sudo nano /etc/network/interfaces
```

At the bottom of that file, add the following:
```bash
auto lo
iface lo inet loopback

auto eth0
iface eth0 inet dhcp

allow-hotplug wlan0
iface wlan0 inet static
    address 192.168.5.1
    netmask 255.255.255.0
    network 192.168.5.0
    broadcast 192.168.5.255
```

Configure hostapd:
```bash
sudo nano /etc/hostapd/hostapd.conf
```
Open /etc/default/hostapd:
```bash
sudo nano /etc/default/hostapd
```
Find the line #DAEMON_CONF="" and replace it with:
```bash
DAEMON_CONF="/etc/hostapd/hostapd.conf"
```
Configure Dnsmasq:
```bash
sudo mv /etc/dnsmasq.conf /etc/dnsmasq.conf.bak
sudo nano /etc/dnsmasq.conf
```

In the new file paste the following: 
```bash
interface=wlan0 
listen-address=192.168.5.1
bind-interfaces 
server=8.8.8.8
domain-needed
bogus-priv
dhcp-range=192.168.5.100,192.168.5.200,24h
```

Restart the Raspberry Rpi:
```bash
sudo reboot
```
Upon rebooting, you can see Pi boots as a WAP and you can see the Rpi's network when you search for avaibale WiFi connections from your devices, Pi shows up.

Now, lets enable packet forwarding for accessing internet via LAN.

Edit the /etc/sysctl.conf file:
```bash
sudo nano /etc/sysctl.conf
```
Look for the line #net.ipv4.ip_forward=1, and uncomment it by deleting the #
```bash
net.ipv4.ip_forward=1

```

Finally, we need to configure Network Address Translation (NAT) between the Ethernet and WiFi interfaces to allow devices on both networks to communicate with each other. In the terminal, enter the following:
```bash
sudo iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE  
sudo iptables -A FORWARD -i eth0 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
sudo iptables -A FORWARD -i wlan0 -o eth0 -j ACCEPT
```

Save the current rules to a file with this command:
```bash
sudo sh -c "iptables-save > /etc/iptables.ipv4.nat"
```

This will work for now, but on reboot, the Pi will revert back to its previous state. To fix this, we need these NAT rules to be applied each time it starts up. So, let us run the above file upon boot. We recommend using rc.local file for this:
```bash
sudo nano /etc/rc.local
```

Just above the exit 0 line (which ends the script), add the following:
```bash
iptables-restore < /etc/iptables.ipv4.nat 
```
Restart Rpi:
```bash
sudo reboot
```

Connect an Ethernet cable from your Internet router (or switch, etc.) to your Pi. Once the Pi boots up, connect to the MyPiAP network from your computer. Open a web browser, and navigate to the website of your choice.

## Running a Node Server on Rpi

Why use Node ?

Node.js is a JS runtime environment which allows the infrastructure to build and run an application. It’s a light, scalable, and cross-platform way to execute code. It uses an event-driven I/O model which makes it extremely efficient and makes scalable network application possible.

It provides great performance and scalibility, cross platform development, ever growing npm enterprise and great support from the community. Further details follows [node.js](https://nodejs.org/en/docs/)

### Installation

A very good tutorial series for installing node and npm is [here](https://www.instructables.com/id/Install-Nodejs-and-Npm-on-Raspberry-Pi/).

Since, we faced a few node versioning issues, we recommend you to try other installation methods like [here](https://www.digitalocean.com/community/tutorials/how-to-install-node-js-on-ubuntu-16-04) in case the later doesn't work for you. We used the latest version of Node (v10.15.3).

Now, once you have set up your node server and have spanned your network, you can clone the following [Our repository/piServer](https://github.com/soumya-ranjan-sahoo/accessiblepricetag) and use the following :

1. piServer.js file : It has the server script for running a Node server and handling the touch event request-response and handshakes with all the connected devices (wemos and android devices).


Run the server script:
```bash
sudo node piServer.js
```

## Wemos Mini Touch Events

Build a simple capactive touch circuit using a Cu/Al foil. In our experiment we used Cu foil for better responsivity. [TouchCircuit](https://playground.arduino.cc/Main/CapacitiveSensor/)

Now that you have a touch circuit, burn the following code [Our repository/price_tag_wemos1](https://github.com/soumya-ranjan-sahoo/accessiblepricetag) to the wemos mini (We used 2 wemos minis for our experiment). Once the code is burned and suitable COM PORT and Baud rate are set, you should be able to view the touch values on your serial monitor of the IDE. 

## Android

The android app can be installed either by the APK file provided above [code](https://github.com/soumya-ranjan-sahoo/accessiblepricetag/tree/master/main) or by building the code via Android studio with your device as the target. (Provided APK is in debug mode.)

Estimote app is required to be pre-installed via PlayStore on the Android device. [estimote](https://estimote.com/?gclid=CjwKCAjwndvlBRANEiwABrR32J1tbCsnUbETLmv2uhqp8TmcLkwJDbsrIoUi9gVG5LQOpnyaa-sbIxoCKFIQAvD_BwE)

Bluetooth and Location services must be enabled for the app to work. If not enabled, the app prompts for it. Allow those permissions. 

This app shows an image and a description of the product that was touched, along with a speech output and a notification. 

This app works when the mobile device is locked, but not when the app is killed. 

To extend this code, see the comments in the code to add more functionality (increasing the products or adding the distance based algorithm etc.)



