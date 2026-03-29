# DashDecide

**Drive smarter, earn more.**

DashDecide is a real-time decision assistant for DoorDash drivers that analyzes incoming offers and provides instant, voice-powered recommendations—keeping drivers safe on the road while maximizing their earnings.

---

## Inspiration

My partner and I started DoorDashing two months ago. When we first started, we quickly realized three critical things we never anticipated:

**First**, the hidden costs add up fast. Gas is expensive. Car insurance is expensive. Car depreciation is real. These costs can eat into earnings faster than you'd expect.

**Second**, not all offers are created equal. There are good offers and bad offers. As an experienced dasher trying to maximize earnings, you need to know which offers are worth taking and which are lowball offers that waste your time. Get it wrong, and you're not just missing out on earning potential—you could actually *lose* money after accounting for gas, insurance, taxes, car depreciation, and the time spent driving back to your zone.

**Third**, and most critically, we found it dangerously difficult to stay safe on the road while evaluating offers. DoorDash drivers receive offer notifications *while driving*. They then have just 40 seconds to read the offer details, study the map, calculate $/mile and $/hour in their head, make a decision, and tap accept or decline—all while keeping their hands on the wheel and eyes on the road. We believe this is a serious safety hazard.

That's why I created **DashDecide**: a tool designed with dashers' safety in mind first. DashDecide also helps drivers optimize their earnings through instant insights on every order, so they can make well-informed decisions without taking their eyes off the road.

---

## What it does

DashDecide is a real-time decision assistant for DoorDash drivers that overlays critical delivery metrics directly on incoming order notifications. When an offer appears, our app instantly displays key insights including:

- Dollars per mile
- Dollars per hour
- Total payout
- Estimated time
- Distance
- Number of dropoffs

The overlay uses a simple verdict system—**ACCEPT** (green) or **DECLINE** (red)—based on configurable thresholds that match each driver's preferences and personal earning goals.

For added safety, DashDecide includes **voice announcements** that read out the verdict and key metrics, enabling completely hands-free operation while driving. Drivers can quickly glance at the overlay or rely on audio cues without ever touching their phone.

By providing instant, at-a-glance and hands-free analysis, DashDecide eliminates guesswork and helps drivers make confident, data-driven decisions in seconds. This means more profitable shifts, less time wasted on low-value orders, and safer driving without the pressure to rush, accept every offer, or interact with their device.

---

## How we built it

**Accessibility Service Monitoring** - The app uses Android's Accessibility Service to continuously monitor the screen. When a DoorDash offer pops up, the service is triggered and automatically pulls critical data from the offer screen, including: payout amount, distance in miles, number of dropoffs, and the "Deliver by" deadline displayed by DoorDash.

**Real-time Calculation** - To calculate estimated delivery time, we compare the current time (pulled from the status bar when the offer appears) against the "Deliver by" time shown in the offer. This gives us the time window. The app then calculates dollars per mile, dollars per hour, and gives a recommendation on whether drivers should take the offer or not.

**Overlay System** - Using Android's TYPE_APPLICATION_OVERLAY permission, we display insights as a floating card on top of the DoorDash app. The overlay shows a large card first with all calculated insights for a quick glance. The big card also shrinks to a small pill that stays in the corner—ensuring it doesn't hide any part of the offer, not even the map, in case dashers want to inspect the route details themselves.

**Text-to-Speech** - At the same time the overlay appears, we use Android's built-in TextToSpeech class to announce the offer details and recommendation out loud. This allows drivers to listen while driving instead of looking at the screen, keeping their hands on the wheel and eyes on the road.

**Foreground Service** - DashDecideForegroundService runs in the background to keep the accessibility features active while the user is dashing, ensuring the app continues monitoring for offers even when not in the foreground.

### Tech Stack
- Android (Kotlin)
- Accessibility Service API
- WindowManager for overlays
- TextToSpeech API
- Foreground Services

---

## Challenges we ran into

**Accessibility Service Permissions** - One of the biggest challenges was getting the Accessibility Service to work reliably. Samsung devices, in particular, don't trust third-party apps by default and require multiple permission grants and security overrides. We had to navigate through several layers of system permissions (overlay permission, accessibility permission, foreground service permission) and ensure users could actually enable our service despite the warnings.

**Overlay Design & Positioning** - We struggled to design the overlay cards to be glanceable yet non-intrusive. The card needed to display all critical insights at a glance, but couldn't hide any part of the DoorDash offer itself, especially not the map. This was particularly challenging because phone screen structures differ across devices (different screen sizes, aspect ratios, notches, navigation bars). We solved this by implementing a two-stage overlay: a large card for quick insights that shrinks to a small corner pill, ensuring the map and offer details remain fully visible for drivers.

---

## Accomplishments that we're proud of

We're proud of creating a fully functional tool that solves a real problem for DoorDash drivers. Despite tight time constraints, we insisted on prioritizing user experience and safety—every design decision prioritized the driver's needs and wellbeing.

From the intuitive color-coded verdict system to hands-free voice announcements, we built DashDecide with a user-centric approach that puts drivers first. Seeing our overlay work seamlessly in real-time, providing instant insights without requiring phone interaction, validated our commitment to building something genuinely useful and safe.

---

## What we learned

**Overlay UI design requires careful UX thinking** - We learned that designing overlays isn't just about displaying information—it's about timing, positioning, and user flow.

**There is always space for improvements to make the tool better, to serve the user better** - We constantly discovered new ways to enhance the user experience. From adjusting speech rates for clarity, to refining the calculation logic, to perfecting the overlay animations—we learned that even a working prototype can continuously evolve. The feedback loop of testing as real dashers ourselves showed us that the best product comes from listening to users and iterating relentlessly.

---

## What's next for DashDecide

**Real DoorDash Integration** - Move beyond demo mode to work with actual live DoorDash offers.

**Machine Learning for Personalized Recommendations** - Build a model that learns from each driver's history to provide increasingly personalized recommendations over time.

**Expanded Metrics** - Add insights like estimated gas cost per trip, profit after expenses, and hotspot recommendations.

**Multi-Platform Support** - Expand to support Uber Eats, Grubhub, Instacart, and other gig economy platforms.

**Driver Community Features** - Add anonymized data sharing so drivers can compare offers and identify trends in their area.

**iOS Version** - Build an iOS version to serve iPhone users.

**Safety Improvements** - Refine the voice interface and explore Android Auto integration for hands-free operation.

---

## Demo

https://youtube.com/shorts/gJl7vE8TidI?si=38JPEGtYriIXDhor
---

## Installation

1. Clone the repository
```bash
git clone https://github.com/vionette/dashdecide_hackusf2026.git
```

2. Open in Android Studio

3. Build and run on an Android device (Android 8.0+)

4. Grant required permissions:
   - Overlay permission
   - Accessibility service permission
   - Foreground service permission

5. Click "TRY DEMO" to see the app in action

---

## Team

Built in 24 hours for HackUSF 2026

---

## License

This project was created for HackUSF 2026.

---

**DashDecide - Drive smarter, earn more.**
