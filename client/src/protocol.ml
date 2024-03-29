(*******************************************************************************)
(*                            Protocol Commands                                *)
(*******************************************************************************)

open Game

(** Parse a single coordinate from a string *)
let coord_of_string s =
  let strings = String.split_on_char 'Y' s in
  let xs = (String.sub (List.nth strings 0) 1 ((String.length (List.nth strings 0))-1))
  and ys = List.nth strings 1 in
  (float_of_string xs, float_of_string ys)

(** Parse a list of coordinates *)
let coords_of_string s =
  let strings = String.split_on_char '|' s in
  match strings with
  | [ "" ] -> []
  | _ -> List.map coord_of_string strings

(** Parse set of coordinates for several players *)
let player_coords_of_string s =
  let parts = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, coord_of_string (List.nth l 1))) parts

(** Parse set of v-coordinates for several players *)
let vcoords_of_string s =
  let parts = String.split_on_char '|' s in
  let r = Str.regexp "\\([a-zA-Z0-9]+\\):X\\([-0-9.E]+\\)Y\\([-0-9.E]+\\)VX\\([-0-9.E]+\\)VY\\([-0-9.E]+\\)A\\([-0-9.E]+\\)" in
  List.map (fun s ->
      if (not (Str.string_match r s 0)) then invalid_arg (Printf.sprintf "vcoords_of_string: %s" s);
      ((Str.matched_group 1 s),
       (float_of_string (Str.matched_group 2 s), float_of_string (Str.matched_group 3 s)),
       (float_of_string (Str.matched_group 4 s), float_of_string (Str.matched_group 5 s)),
       float_of_string (Str.matched_group 6 s))) parts

(** Parse set of v-coordinates for several bullets *)
let bulletvcoords_of_string s =
  if s = "" then [] else (
    let parts = String.split_on_char '|' s in
    let r = Str.regexp "X\\([-0-9.E]+\\)Y\\([-0-9.E]+\\)VX\\([-0-9.E]+\\)VY\\([-0-9.E]+\\)A\\([-0-9.E]+\\)" in
    List.map (fun s ->
        if (not (Str.string_match r s 0)) then invalid_arg (Printf.sprintf "bulletvcoords_of_string: %s" s);
        ((float_of_string (Str.matched_group 1 s), float_of_string (Str.matched_group 2 s)),
         (float_of_string (Str.matched_group 3 s), float_of_string (Str.matched_group 4 s)),
         float_of_string (Str.matched_group 5 s))) parts
  )

(** Commands from Client to Server *)
type clientcmd =
  (* Partie A *)
  | Connect of string
  | Exit of string
  | NewPos of coord
  (* Partie B *)
  | NewCom of float * int
  (* Extension : calcul coté client *)
  | NewPosCom of (float * float) * float * int
  (* Extension: jeu de combat *)
  | NewBullet of coord * coord * float

(** Translate a client command to a string, to send it to the server *)
let string_of_clientcmd = function
  | Connect u -> Printf.sprintf "CONNECT/%s/" u
  | Exit u -> Printf.sprintf "EXIT/%s/" u
  | NewPos (x, y) -> Printf.sprintf "NEWPOS/X%fY%f/" x y
  | NewCom (angle, thrust) -> Printf.sprintf "NEWCOM/A%fT%d/" angle thrust
  | NewPosCom ((x, y), angle, thrust) -> Printf.sprintf "NEWPOSCOM/X%fY%f/A%fT%d/" x y angle thrust
  | NewBullet ((x, y), (vx, vy), angle) -> Printf.sprintf "NEWBULLET/X%fY%f/VX%fVY%f/A%f/" x y vx vy angle

(** Liste de scores *)
type scores = (string * int) list

let scores_of_string s =
  let scores = String.split_on_char '|' s in
  List.map (fun s -> let l = String.split_on_char ':' s in
             (List.nth l 0, int_of_string (List.nth l 1))) scores

(** Commands from Server to Client *)
type servercmd =
  | Unknown
  (* Partie A *)
  | Welcome of phase * scores * coord list * coord list
  | Denied
  | NewPlayer of string
  | PlayerLeft of string
  | Session of (string * coord) list * coord list * coord list
  | Winner of scores
  | Tick of (string * coord * coord * float) list
  | NewObj of coord * scores
  (* Extension: jeu de combat *)
  | BulletTick of (coord * coord * float) list
  | Stun of float

(** Parses a string to a server command *)
let servercmd_of_string s =
  let parts = String.split_on_char '/' s in
  match (List.hd parts) with
  | "WELCOME" -> Welcome (phase_of_string (List.nth parts 1),
                          scores_of_string (List.nth parts 2),
                          coords_of_string (List.nth parts 3),
                          coords_of_string (List.nth parts 4))
  | "DENIED" -> Denied
  | "NEWPLAYER" -> NewPlayer (List.nth parts 1)
  | "PLAYERLEFT" -> PlayerLeft (List.nth parts 1)
  | "SESSION" -> Session (player_coords_of_string (List.nth parts 1),
                          coords_of_string (List.nth parts 2),
                          coords_of_string (List.nth parts 3))
  | "WINNER" -> Winner (scores_of_string (List.nth parts 1))
  | "TICK" -> Tick (vcoords_of_string (List.nth parts 1))
  | "NEWOBJ" -> NewObj ((coord_of_string (List.nth parts 1)),
                        (scores_of_string (List.nth parts 2)))
  | "BULLETTICK" -> BulletTick (bulletvcoords_of_string (List.nth parts 1))
  | "STUN" -> Stun (float_of_string (List.nth parts 1))
  | _ -> Unknown

(** Execute a server command *)
let execute_command = function
  | Welcome (phase, scores, objCoords, obsCoords) -> (
      Mutex.lock stateMut;
      state.phase <- phase;
      state.scores <- scores;
      state.player.score <- snd (List.find (fun (u, _) -> u = state.player.username) scores);
      state.objCoords <- objCoords;
      state.obsCoords <- obsCoords;
      Mutex.unlock stateMut;
      Interface.display_welcome ()
    )
  | Denied -> print_endline "The connection was denied"; exit 1
  | NewPlayer u -> print_endline (Printf.sprintf "Player %s just connected" u)
  | PlayerLeft u -> print_endline (Printf.sprintf "Player %s just left" u)
  | Session (coords, objCoords, obsCoords) -> (
      Mutex.lock stateMut;
      state.phase <- Jeu;
      state.coords <- List.map (fun (u, c) -> (u, c, (0.,0.), 0.)) coords;
      List.iter (fun (u, coord) -> if (u = state.player.username) then state.player.coord <- coord) coords;
      state.objCoords <- objCoords;
      state.obsCoords <- obsCoords;
      state.player.score <- 0;
      state.player.speed <- (0., 0.);
      state.bullets <- [];
      Mutex.unlock stateMut;
    )
  | Tick coords -> (
      Mutex.lock stateMut;
      state.coords <- coords;
      List.iter (fun (u, c, s, a) -> if u = state.player.username
                  then (state.player.coord <- c; state.player.speed <- s; state.player.angle <- a)) coords;
      (* send (NewPos state.player.coord) *) (* Answer, partie A *)
      Mutex.unlock stateMut
    )
  | NewObj (_, scores) -> (
      Mutex.lock stateMut;
      (* state.objCoords <- [coord]; *)
      state.scores <- scores;
      state.player.score <- snd (List.find (fun (u, _) -> u = state.player.username) scores);
      Mutex.unlock stateMut;
      Interface.display_scores state.scores
    )
  | Winner scores -> (
      Mutex.lock stateMut;
      state.scores <- scores;
      state.player.score <- snd (List.find (fun (u, _) -> u = state.player.username) scores);
      state.phase <- Attente;
      state.player.speed <- (0., 0.);
      Mutex.unlock stateMut;
      Interface.display_winner ()
    )
  (* Extension: jeu de combat *)
  | BulletTick b -> (
      Mutex.lock stateMut;
      state.bullets <- b;
      Mutex.unlock stateMut
    )
  | Stun f -> (
      Mutex.lock stateMut;
      state.player.stunTime <- f;
      Mutex.unlock stateMut
    )
  | Unknown -> failwith "Unknown command"
