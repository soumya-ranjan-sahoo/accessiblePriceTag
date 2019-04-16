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
denyinterfaces wlan0

Scroll down, and at the bottom of the file, add:
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

Configure hostapd
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
Configure Dnsmasq
```bash
sudo mv /etc/dnsmasq.conf /etc/dnsmasq.conf.bak
sudo nano /etc/dnsmasq.conf
```

In the  new file paste the following : 
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

Upon rebooting, you can see Pi boots as a WAP and you can see the Rpi's network when you search for avaibale WiFi connections from your devices.



