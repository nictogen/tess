# tess
A bot coded in kotlin that adds a traveling function to discord, for rp server use.


##How to use:

1: Invite tess to your server:
https://discordapp.com/api/oauth2/authorize?client_id=370841344706347008&scope=bot&permissions=0

2: Give tess administrator permissions:
Or really only managing channels, messages, reactions.

3: Add a location:
Use this format to add a new location

@tess new <discord emoji> <name>

4: Travel to your location:
Tag tess to bring up the travel message, click on the corresponding emoji reaction to travel to it

5: Add areas to your location:
Just create channels in the new category that popped up. Now, anyone who travels to that location can see those channels.

6: Repeat steps 3-6 as much as you want 

7: Enjoy


Extra info:

To delete a location, simply travel to it and delete the category.

To delete an area, simply delete the channel that represents it

Adding more than one area in the same location with the same name won't really work

Adding more than one location with the same emoji won't really work

If no one is in a location, it's category and all the channels inside of it are temporarily deleted until someone travels to it again.
This is to help overcome the 100 channel limit.

Accounts that have the administrator permission or are the owner won't really get to use the travel system, as they can already see every channel. 
