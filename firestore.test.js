const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Unauthenticated user: cannot read scans", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("scans").get());
});

test("Authenticated user: cannot read another farmer's profile or scans", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      displayName: "Ramesh Patel",
      preferredLanguage: "gu",
      createdAt: new Date(),
    });
    await context.firestore().collection("users").doc(BOB_UID).collection("scans").doc("scan_1").set({
      userId: BOB_UID,
      cropName: "Cotton",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("scans").doc("scan_1").get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("scans").get());
});

test("Authenticated user: can create and read their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      displayName: "Pravinbhai",
      preferredLanguage: "gu",
      district: "Rajkot",
      primaryCrops: ["Cotton", "Groundnut"],
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create and list their own crop scans", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("scans").doc("scan_123").set({
      userId: ALICE_UID,
      cropName: "Cotton",
      cropStage: "Flowering",
      symptomsDescription: "Yellow curling leaves with tiny spots",
      district: "Amreli",
      visibleSymptoms: ["Leaf yellowing", "Curl"],
      possibleCauses: ["Whitefly infestation", "Nutrient deficiency"],
      uncertaintyLevel: "Medium",
      missingInformation: ["Underside leaf photo"],
      safeNextSteps: ["Inspect leaf undersides for pests", "Avoid premature broad pesticide spray"],
      expertReferralRequired: false,
      disclaimer: "Guidance only. Confirm with local KVK specialist.",
      language: "gu",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("scans").get()
  );
});

test("Authenticated user: can add and read follow up scans", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("follow_ups").doc("fu_123").set({
      userId: ALICE_UID,
      scanId: "scan_123",
      cropName: "Cotton",
      visibleDifferences: ["Slightly reduced yellowing"],
      updatedObservations: "Leaf curl appears stabilized",
      suggestedNextSteps: ["Continue monitoring weekly"],
      farmerNotes: "Sprayed neem oil 4 days ago",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("follow_ups").get()
  );
});

test("Anti-spoofing: cannot write scan with mismatched userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("scans").doc("scan_fake").set({
      userId: BOB_UID,
      cropName: "Cotton",
      createdAt: new Date(),
    })
  );
});
