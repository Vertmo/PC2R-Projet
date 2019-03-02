(*******************************************************************************)
(*               Current state of the game, as known by the player             *)
(*******************************************************************************)

(** Half-width and Half-height of the arena *)
let w = 350 and h = 300

(** Variables controlling rotation and acceleration *)
let turnit = 0.2 and thrustit = 0.2

(** Phase: attente or jeu *)
type phase = Attente | Jeu

let phase_of_string = function
  | "attente" -> Attente | "jeu" -> Jeu | _ -> failwith "Unknown phase"

(** Player state (coordinates, speed and score) *)
type playerState = {
  mutable username: string;
  mutable coord: float * float;
  mutable speed: float * float;
  mutable angle: float;
  mutable score: int;
}

(** State of the whole game *)
type gameState = {
  mutable coords: (string * (float * float) * (float * float) * float) list;
  mutable scores: (string * int) list;
  mutable phase: phase;
  mutable objCoord: (float * float);
  mutable player: playerState;
}

let state = {
  coords = [];
  scores = [];
  phase = Attente;
  objCoord = (0., 0.);
  player = {
    username = "";
    coord = (0., 0.);
    speed = (0., 0.);
    angle = 0.; (* The angle is in radians ! *)
    score = 0;
  }
}

let stateMut = Mutex.create ()

let newCom: (float * int) option ref = ref None

(** Move the vehicle *)
let move () =
  Mutex.lock stateMut;
  let (x, y) = state.player.coord and (vx, vy) = state.player.speed in
  state.player.coord <- (mod_float (x+.vx) (float_of_int (2*w)),
                         mod_float (y+.vy) (float_of_int (2*h)));
  state.coords <- List.map (fun (u, (x, y), (vx, vy), a) -> (u, (
      mod_float (x+.vx) (float_of_int (2*w)),
      mod_float (y+.vy) (float_of_int (2*h))
    ), (vx, vy), a)) state.coords;
  Mutex.unlock stateMut

(** Augment speed of the vehicle *)
let thrust () =
  Mutex.lock stateMut;
  (* Partie A *)
  (* let (vx, vy) = state.player.speed and theta = state.player.angle in
   * state.player.speed <- (vx+.thrustit*.(cos theta),
   *                       vy+.thrustit*.(sin theta)); *)

  (* Partie B *)
  (match !newCom with
   | None -> newCom := Some (0., 1)
   | Some (angle, thrust) -> newCom := Some (angle, thrust+1));
  Mutex.unlock stateMut

(** Turn clockwise *)
let clock () =
  Mutex.lock stateMut;
  (* Partie A *)
  (* state.player.angle <- state.player.angle -. turnit; *)

  (* Partie B *)
  (match !newCom with
   | None -> newCom := Some (-. turnit, 0)
   | Some (angle, thrust) -> newCom := Some (angle -. turnit, thrust));
  Mutex.unlock stateMut

(** Turn anticlockwise *)
let anticlock () =
  Mutex.lock stateMut;
  (* Partie A *)
  (* state.player.angle <- state.player.angle +. turnit; *)

  (* Partie B *)
  (match !newCom with
   | None -> newCom := Some (turnit, 0)
   | Some (angle, thrust) -> newCom := Some (angle +. turnit, thrust));
  Mutex.unlock stateMut
