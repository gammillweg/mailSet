# mailSet
# Temporary chrippling of mailSet (220412)
- I have deleted modules configPrep and mailPrep (all java code)
- I got ahead of myself.  I need to do a lot more work on my local system
  before I release my code to the world. (Hopefully before the end of
  summer.)
- If mailSet is not back by Dec 2022, then it probably will never
  be posted.
- (OK, I understand, the code is not really deleted.  GitHub has it.
  I could delete the repository; but choose not to.  But, understand,
  if you pull the code committed prior to my chrippling.  It is not
  all that useful.  Just wait.  Hopefull I'll finish my work. and
  push good stuff up.)
  
### Presorted US Mail PS3541 Processing via Excel and a set of Java apps.

The Denver Area Square Dance Council has been publishing a Square Dance
Bulletin, a book of some 100-120 pages for 30 to 40 years, pre-sorted
and delivered to the USPS using PS3541.  This procedure can be used
by clubs or small businesses to process there mail lists using
volunteers.  There are businesses set up to do the job for a fee.
And no doubt, using a mail service is easier; but certainly more expensive
than doing it yourself.  The Denver Square Dance Bulletin has been
volunteer published and pre-sorted for a long time.  It can be done.
The documents here are for a periodical pre-sort, with an issue at least
100 pages long.  But there are postal forms for fewer pages suitable
for use by newsletters.  Pre-sorting saves considerable postage.

#### About the organization of this repository
- My local git repository records under the directory structure of Intellij.
- I like to use package folders to organize my work, and I used Intellij 
modules under two projects.  Consequently, one has to step around quite
a lot to find the java code.  I have considered uploading a "flat" set of
java files, just for convenience’s sake.
- If you clone mailSet and open with Intellij IDEA, it loads and builds.
I use IntelliJ IDEA (Community Edition) (current build: 2021.3.2)

### There are two parts to the processing presented here:
   1) a set of Excel worksheets and
   2) a set of Java apps to support the Excel worksheets.

All the examples, worksheets, zip codes, zones and forms are
based from zone 802, Denver CO.

### Excel Document
- MasterList_LXIV.xlxs   (LXIV is the Annual Volume number)

#### WorkSheets:

- Addrlist:        A data base of subscribers sorted by expiration date.
                   Obvious fake names and addresses; but the zip codes
                   are real.
- ZipCodes:        A list of zip codes.  Current and past zip codes
                 Used to count subscribers, due to expire subscribers
                 and complimentary issues.
- ZipCounts:       A number of postal zone tables, a master accumulation
                 table including the PartC table, and a few supporting
                 look-up tables.  As well as monthly issue data collected
                 for an annual report to the USPS.
- BalancePS3541:   USPS Form PS3541 No Bar Code Periodical.  A copy
                 of the master accumulation table from ZipCounts.
                 Balance is the historical name used for active or
                 current subscribers.
- RenewalPS3541    USPS Form PS3541 No Bar Code Periodical.  A copy
                 of the master accumulation table from ZipCounts.
                 Renewal is the historical name used for the subscriber
                 list that will expire with the current issue.
                 (Two forms are used.  Explained below.)
- Expired:         Historical record of past subscribers.  Keeping subsriber
                 names makes it easier to move them back to the active
                 list when they resubscribe.  Here, this worksheet is empty.
- Packing List:    A shipping count by Postal Tray
- Audit PS3526     USPS Form PS3526, an annually required review of
                 issues processed. (It is not really an audit.)

- Word Templates:  For printing address labels.

One must regularly check http://www.usps.com/forms/_pdf/ps3541.pdf
and update worksheets BalancePS3541 and RenewalsPS3541 to reflect
USPS price changes and occasional changes to the form.

Two separate PS3541 are given to the USPS.  We spit the shipment as
we insert a renewal notice when a users subscription is about to
expire.  The additional page adds weight to the issue.  Thus the
calcualtions are slightly changed.  The USPS allows us to mix the
renewal issues with the current issues; but require two sets of
forms. If you implemnet, you may only need one form.

The two PS3541 forms are the principle reason for mailSet.  Excel
does all the math to fill the two forms out.  To "close the books"
prior to assembly, one "simply" insures all is correct in the Excel
DB, copies, by value, the accumulation table from worksheet ZipCounts
to each of the postal forms, and the the forms are updated with
the latest data.  Print and submit with your presorted mail.  Labels
for each issue is a bonus.  The real work is the postal forms.

There is also an Accounting.xlsx; but it is nothing special.
Run-of-the-mill collect checks and total for deposit and
charges to petty cash.


### Java Applications
- IDE: Intellij IDEA Community Edition 2021.3.2

#### Project: configPrep
- genConfigFile:   There is a required config.xml file.  Code exists to
                 automatically generate a default config.xml.  Further,
                 this app will read an existing config.xml and generate
                 java code to generate a default config.xml, thus
                 easing the effort to keep the default current.
- genConfigOMX:    Reformat the USPS Web list to that required by
                 the config file.
                 The USPS web post (as of 2022/March) Quick Service Guide
                 http://pe.usps.gov/text/qsg300/Q207d.htm#1009536
                 Find L201 Periodicals Origin Split First-Class Mail
                 Mixed ADC/AACD, and find your City (802 Denver).
                 Copy and paste to a file the middle column.
- genConfigZones:  Reformats the USPS Web list to that required by
                 the config file.
                 The USPS web posts at https://postcalc.usps.com/DomesticZoneChart
                 Enter your "In County" Zip (802 Denver)
                 Copy the table, including the ZIP CODE/ZONE headings,
                 and paste to a file.
- Neither change very often.  Zones more often than OMX.  But each should
be updated every two or three months.  The code checks a date in the
config file and warns you if it has not been updated lately.

#### Project mailPrep
- chkZipCodes:     Reads the csv files created from worksheets AddressList
                 and ZipCodes.  It verifies that all current subscriber
                 zip codes are accounted for in worksheet ZipCodes, and
                 generated an error list if any are found missing.
- chkZipCounts:    Reads the csv files created from worksheets AddressList
                 and ZipCounts and config.xml and verifies that all
                 worksheet ZipCounts zone tables are correct.  Checks for
                 missing, erroneous or duplicated zip codes in each table.
- genPartC:        There was difficulty filling out PS3541 Part C using
                 Excel.  A software solution was found.  GenPartC computes
                 data to fill out Zip Counts accumulation table, Part C
                 sub table.
- genLabels:       Generates a csv file formatted for MS Word Mail Merge.
                 and pre-sort assembly envelope labels for use by
                 the assembly crew while pre-sorting and applying address
                 labels to issues.  Also generates a checklist for
                 quality control during the pre-sort (we call it "Assembly").
                 And a cutting guide to help cut up the label sheets for
                 separating into "label envelopes."  (I forgot the need
                 for a packing list.  genLabels will need to be expanded
                 to generate a packing list.)
- All these apps are small and focused; except for genLabels, which catches
  all the assembly needs.   

None of the above Java code is special.  It is all common run of the
mill code, just chugging along to do it's job.

To implement this procedure for your club or small business will not
be a easy task.  It can be done; but you will need a core group
dedicated to making it happen.  Having a person or persons skilled in
Exel and Java would be helpful, if not required.

I have tried to avoid the word "I".  But this is all from one person,
Bill G.  I wrote all the code.  I did not do the original Excel work, I
built on the work of two predecessors.  But much of the Excel work is
on me.  I am approaching 80 years old, and I want to pass my work on.  I
am not willing to devote a lot of time helping some club or business
set themselves up.  I am not trying to go into business.  But I have
provided an email address and, assuming there will not be many who
request my services, I will try to help.  I really don't expect
anybody, not even one person, to read this paper, download the code or
try to implement the pre sort process.  But, one never knows.

### A tad bit of history

Some 30 years ago Joe N started the Excel address list and came up
with the idea of filling out the Postal form within Excel.  He passed
away and Ron M took his place.  Ron refined the Excel forms.  Ron did
the work for about 10 years then passed the job onto me.  The postal
from was one sheet wide by the number of pages (rows) down.  It was
hard to modify if the USPS changed the form.  I spread the from
diagonally so each sheet of the form could be modified independent of
the others.  All the data was maintained on the one work sheet.
I broke it into ZipCodes and ZipCounts, created the Zone tables and
accumulation table.  Later wrote the code for PartC when my
programming skills exceeded my Excel skills.

From the beginning I wanted to slice the labels with a paper cutter,
and intended to write a VS C# Forms program to do that.  I wrote
BulletinSlicer.  Originally it did the above and created labels for
the Assembly envelopes to help volunteers do the work of applying
labels to issues.  The program grew to checking for missing zip codes
in ZipCode and verify zips per zone in ZipCounts.  Then to formatting
USPS web data for the config file.

I left Windows sometime after Windows 10 came out and moved back to my
home base of Unix.  Preferring Linux to Windows.  Moved from VC C# to
Java.  I have used BulletinSlicer for at least 10 years.  I decided to
convert it to Java so I could do my Bulletin work on Linux and not be
required to boot up Windows regularly.  Further, I became concerned I
would have problems passing on a Windows based programs due to
Certificates.  I knew Java would work on any machine and whoever takes
over from me may be a Mac person more than a Windows person.  The
convert became a rewrite.  And I decided to break the one big
BulletinSlicer into several smaller Java apps.

As of this time (early 2022) the Java code has not been used for a
Bulletin Assembly.  I plan to bring it into use next season.  (Our
season starts in September and runs through June.) But, there is a
problem.  Denver Area Square Dancing was hit very hard by Covid 19.
Our staff has aged.  Our circulation is down.  We may stop producing
our Bulletin before I am able to bring mailSet into productive use.
(I hate that thought!)

The Window Forms program, BulletinSlicer is not provided on this
GitHup site.  I don't really want to give it away; but there is
nothing priority about it.  If you prefer an old. well tested,
VC C# Forms program to the provided Java code, let me know, and I'll
think about it.  (Again, I don't expect anyone to ever read this, let
alone ask for BulletinSlicer.)
