# Tap through the sample on devices

What only a device shows. On an Android phone and an iPhone (simulator is not enough for the gestures):

- the backdrop scales back as a sheet rises and follows a drag;
- a sheet with a list: scroll to the list's top and keep pulling — the sheet follows; fling down from
  the top closes it; pull the sheet part way and push up — the sheet goes back before the list scrolls;
- predictive back (Android 14+, `enableOnBackInvokedCallback`): the dialog shrinks and the sheet sinks
  with the finger, a cancelled gesture restores them; iOS edge swipe closes the top overlay;
- focus: type in the text field, open the dialog — the keyboard closes; close it — focus is back;
  with a hardware keyboard, Tab stays inside the open dialog;
- TalkBack / VoiceOver: with a dialog open, the screen behind cannot be reached; the scrim reads as
  "Dismiss"; a snackbar is announced;
- dropdown: a press outside closes it and still reaches what it landed on; a press on the anchor
  toggles; the menu stays above the keyboard;
- tooltip near the screen's edge slides along it with the caret on the anchor; near the bottom flips
  above;
- showcase cut-out sits on the badge in both orientations.
